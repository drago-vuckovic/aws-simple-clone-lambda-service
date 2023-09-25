package co.vuckovic.lambdaservice.service;

import co.vuckovic.lambdaservice.repository.LambdaEntityRepository;
import co.vuckovic.lambdaservice.repository.LambdaExecutionTimeEntityRepository;
import co.vuckovic.lambdaservice.repository.LogEntityRepository;
import co.vuckovic.lambdaservice.repository.TotalExecutionTimeEntityRepository;
import co.vuckovic.lambdaservice.repository.entity.LambdaEntity;
import co.vuckovic.lambdaservice.repository.entity.LambdaExecutionTimeEntity;
import co.vuckovic.lambdaservice.repository.entity.LogEntity;
import co.vuckovic.lambdaservice.repository.entity.TotalExecutionTimeEntity;
import co.vuckovic.lambdaservice.request.LambdaServiceCreationRequest;
import co.vuckovic.lambdaservice.config.BucketProperties;
import co.vuckovic.lambdaservice.config.DockerProperties;
import co.vuckovic.lambdaservice.config.LambdaProperties;
import co.vuckovic.lambdaservice.model.dto.Lambda;
import co.vuckovic.lambdaservice.model.enumeration.LambdaLang;
import co.vuckovic.lambdaservice.model.exception.ActionNotAllowedException;
import co.vuckovic.lambdaservice.model.exception.ConflictException;
import co.vuckovic.lambdaservice.model.exception.NotFoundException;
import co.vuckovic.lambdaservice.model.exception.ProcessExecutionException;
import co.vuckovic.lambdaservice.model.request.ChangeLambdaFolderRequest;
import co.vuckovic.lambdaservice.model.request.LambdaCheckRequest;
import co.vuckovic.lambdaservice.model.request.LambdaServiceUpdateRequest;
import co.vuckovic.lambdaservice.model.request.LambdaUpdateStatusRequest;
import co.vuckovic.lambdaservice.model.response.ActiveLambdaExecutionTimeResponse;
import co.vuckovic.lambdaservice.model.response.LambdaExecutionTimeResponse;
import co.vuckovic.lambdaservice.model.response.LambdasResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LambdaService {

  private final LambdaProperties lambdaProperties;

  private final BucketProperties bucketProperties;

  private final LambdaEntityRepository lambdaEntityRepository;

  private final LogEntityRepository logEntityRepository;

  private final ModelMapper modelMapper;

  private final DockerProperties dockerProperties;

  private final LambdaExecutionTimeEntityRepository lambdaExecutionTimeEntityRepository;

  private final TotalExecutionTimeEntityRepository totalExecutionTimeEntityRepository;

  public Integer processLambdaFile(MultipartFile file, LambdaServiceCreationRequest request)
      throws IOException, InterruptedException {

    LambdaEntity lambdaEntity = modelMapper.map(request, LambdaEntity.class);
    lambdaEntity.setId(0);
    lambdaEntity.setIsEnabled(true);
    lambdaEntity.setLambdaFilePath("");
    lambdaEntity = lambdaEntityRepository.save(lambdaEntity);

    createLambdaDirInFakeDestDir(lambdaEntity.getId());

    LogEntity logEntity = LogEntity.builder().lambdaId(lambdaEntity.getId()).id(0)
        .startTime(Timestamp.from(Instant.now())).build();
    logEntityRepository.save(logEntity);

    File lambdaEntityDir = new File(
        lambdaProperties.getDirPath() + File.separator + lambdaEntity.getId());
    lambdaEntityDir.mkdir();

    File lambdaFile = lambdaEntityDir.toPath()
        .resolve(Objects.requireNonNull(file.getOriginalFilename())).toFile();

    Files.write(lambdaFile.toPath(), file.getBytes());

    switch (lambdaEntity.getLambdaLang()) {
      case JAVA -> {
        runProcess("javac -cp src " + lambdaFile.getAbsolutePath() + " -d "
            + lambdaEntityDir.getAbsolutePath());

        String lambdaClassFilePath =
            FilenameUtils.removeExtension(
                lambdaFile.getAbsolutePath()) + ".class";
        lambdaEntity.setLambdaFilePath(lambdaClassFilePath);
      }
      case PYTHON, CPP -> lambdaEntity.setLambdaFilePath(lambdaFile.getAbsolutePath());

      default -> throw new NotFoundException("Language not supported");
    }
    lambdaEntityRepository.save(lambdaEntity);

    return lambdaEntity.getId();
  }

  private void createLambdaDirInFakeDestDir(Integer lambdaId) {
    File lambdaDir = new File(
        lambdaProperties.getFakeDestDir() + File.separator + lambdaId);
    lambdaDir.mkdir();
  }

  private void runProcess(String command) throws IOException, InterruptedException {
    Process pro = Runtime.getRuntime().exec(command);
    pro.waitFor();
    if (pro.exitValue() != 0) {
      log.error("Process failed for command:" + command);
      throw new ProcessExecutionException("An error occurred while compiling lambda");
    }
  }

  public void updateLambda(Integer id, MultipartFile multipartFile,
      LambdaServiceUpdateRequest lambdaServiceUpdateRequest)
      throws IOException, InterruptedException {

    LambdaEntity lambdaEntity = lambdaEntityRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Lambda not found"));

    if (!lambdaEntity.getCreatedBy().equals(lambdaServiceUpdateRequest.getCurrentUserEmail())) {
      throw new ActionNotAllowedException("Only owner can update lambda!");
    }

    LambdaEntity lambdaEntityUpdate = modelMapper.map(lambdaServiceUpdateRequest,
        LambdaEntity.class);
    lambdaEntityUpdate.setId(lambdaEntity.getId());
    lambdaEntityUpdate.setTenantId(lambdaEntity.getTenantId());
    lambdaEntityUpdate.setLambdaFilePath(lambdaEntity.getLambdaFilePath());
    lambdaEntityUpdate.setCreationTime(lambdaEntity.getCreationTime());
    lambdaEntityUpdate.setCreatedBy(lambdaEntity.getCreatedBy());
    lambdaEntityUpdate.setIsEnabled(lambdaEntity.getIsEnabled());
    lambdaEntityUpdate.setLogEntities(lambdaEntity.getLogEntities());

    if (multipartFile != null) {
      File lambdaEntityDir = new File(
          lambdaProperties.getDirPath() + File.separator + lambdaEntityUpdate.getId());

      File lambdaFile = lambdaEntityDir.toPath()
          .resolve(Objects.requireNonNull(multipartFile.getOriginalFilename())).toFile();

      Files.write(lambdaFile.toPath(), multipartFile.getBytes());

      if (lambdaEntity.getLambdaLang().equals(LambdaLang.JAVA)) {
        runProcess("javac -cp src " + lambdaFile.getAbsolutePath() + " -d "
            + lambdaEntityDir.getAbsolutePath());
      }

    }
    lambdaEntityRepository.save(lambdaEntityUpdate);
  }

  public void updateLambdaStatus(Integer id, LambdaUpdateStatusRequest lambdaUpdateRequest) {
    LambdaEntity lambdaEntity = lambdaEntityRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Lambda not found"));
    Boolean oldStatus = lambdaEntity.getIsEnabled();
    lambdaEntity.setIsEnabled(lambdaUpdateRequest.getIsEnabled());
    lambdaEntityRepository.save(lambdaEntity);

    if (!oldStatus.equals(lambdaUpdateRequest.getIsEnabled()) && Boolean.TRUE.equals(
        lambdaEntity.getIsEnabled())) {
      logEntityRepository.save(
          new LogEntity(0, Timestamp.from(Instant.now()), null, lambdaEntity.getId()));
    } else if (!oldStatus.equals(lambdaUpdateRequest.getIsEnabled()) && Boolean.FALSE.equals(
        lambdaEntity.getIsEnabled())) {
      LogEntity lastLog = lambdaEntity.getLogEntities()
          .get(lambdaEntity.getLogEntities().size() - 1);
      lastLog.setEndTime(Timestamp.from(Instant.now()));
      logEntityRepository.save(lastLog);
    }

  }

  public List<LambdaExecutionTimeResponse> getExecutionTimes(
      String filter, Integer tenantId) {

    List<LambdaExecutionTimeResponse> lambdaExecutionTimeResponses;
    Timestamp endTime = Timestamp.from(Instant.now());
    Timestamp startTime;

    switch (filter) {
      case "DAY" -> {
        startTime = Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = logEntityRepository.findAllLambdaExecutionDurations(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, LambdaExecutionTimeResponse.class))
            .toList();
      }
      case "WEEK" -> {
        startTime = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = logEntityRepository.findAllLambdaExecutionDurations(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, LambdaExecutionTimeResponse.class))
            .toList();
      }

      case "MONTH" -> {
        startTime = Timestamp.from(Instant.now().minus(30, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = logEntityRepository.findAllLambdaExecutionDurations(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, LambdaExecutionTimeResponse.class))
            .toList();
      }
      default -> throw new NotFoundException("Interval not supported");

    }

    return lambdaExecutionTimeResponses;
  }


  public LambdasResponse getAllLambdas(Integer tenantId) {
    List<Lambda> lambdas = lambdaEntityRepository.findAllByTenantId(tenantId).stream()
        .map(l -> modelMapper.map(l, Lambda.class))
        .toList();
    lambdas.forEach(l -> {
      if (Boolean.FALSE.equals(l.getSrcPath().isEmpty())) {
        l.setSrcPath(
            l.getSrcPath().substring(bucketProperties.getFolderPath().length()).replace("\\", "/"));
      }
      if (Boolean.FALSE.equals(l.getDestPath().isEmpty())) {
        l.setDestPath(
            l.getDestPath().substring(bucketProperties.getFolderPath().length())
                .replace("\\", "/"));
      }
    });
    return LambdasResponse.builder().lambdas(lambdas).build();
  }

  public boolean checkIfLambdaExists(LambdaCheckRequest lambdaCheckRequest) {
    return lambdaEntityRepository.existsBySrcPathAndTriggerTypeAndIsEnabledTrue(
        lambdaCheckRequest.getSrcDirPath(), lambdaCheckRequest.getTriggerType());
  }

  public void deleteLambda(Integer id) throws IOException {
    LambdaEntity lambdaEntity = lambdaEntityRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Lambda not found"));
    FileUtils.deleteDirectory(
        new File(lambdaProperties.getDirPath() + File.separator + lambdaEntity.getId()));
    lambdaEntityRepository.delete(lambdaEntity);
  }


  public void executeLambda(LambdaLang lang, String lambdaCodePath, String destDirPath,
      byte[] fileBytes, String fileName, Integer lambdaId, Integer tenantId) {
    TotalExecutionTimeEntity totalExecutionTimeEntity = totalExecutionTimeEntityRepository.findByTenantId(
        tenantId).orElseThrow(() -> new NotFoundException("Total execution time not found"));
    if (totalExecutionTimeEntity.getMaxDuration() != null
        && totalExecutionTimeEntity.getDuration() >= totalExecutionTimeEntity.getMaxDuration()) {
      log.error("Runtime max duration can't be exceeded");
      return;
    }

    File newFolder = new File(
        dockerProperties.getDirPathWin() + File.separator + generateRandomString());
    newFolder.mkdir();
    File dockerFile = new File(newFolder + File.separator + dockerProperties.getDockerfile());
    File code = new File(lambdaCodePath);
    File copyOfCode = new File(newFolder.getAbsolutePath() + File.separator + code.getName());
    File file = new File(newFolder.getAbsolutePath() + File.separator + fileName);

    try {
      Files.write(copyOfCode.toPath(), Files.readAllBytes(code.toPath()));
      Files.write(file.toPath(), fileBytes);

      writeDockerFile(copyOfCode.getName(), fileName, lang, dockerFile);
      buildDockerImage(newFolder);
      runDockerContainer(newFolder, destDirPath, lambdaId, tenantId);
      deleteDockerImage(newFolder.getName().toLowerCase(Locale.ROOT));
      FileUtils.deleteDirectory(newFolder);
    } catch (Exception e) {
      log.error(String.format("An error has occurred :%s", e.getMessage()));
    }
  }

  private String generateRandomString() {
    return RandomString.make(25);
  }

  private void writeDockerFile(String lambdaFileName, String newFileName, LambdaLang lang,
      File dockerFile) {
    try (PrintWriter output = new PrintWriter(dockerFile)) {
      switch (lang) {
        case JAVA -> {
          output.println("FROM openjdk:17-oracle");
          output.println("COPY . /src");
          output.println("RUN mkdir /src/output");
          output.println("WORKDIR /src");
          output.println(
              "CMD [\"java\", \"" + FilenameUtils.removeExtension(lambdaFileName)
                  + "\",\"output\", \"" + newFileName + "\"]");
        }
        case PYTHON -> {
          output.println("FROM python");
          output.println("RUN pip install Pillow");
          output.println("COPY . /src");
          output.println("RUN mkdir /src/output");
          output.println("WORKDIR /src");
          output.println(
              "CMD [\"python\", \"" + lambdaFileName + "\",\"output\", \"" + newFileName + "\"]");
        }
        case CPP -> {
          output.println("FROM gcc:4.9");
          output.println("COPY . /src");
          output.println("RUN mkdir /src/output");
          output.println("WORKDIR /src");
          output.println(
              "RUN g++ -o " + FilenameUtils.removeExtension(lambdaFileName) + " " + lambdaFileName
                  + " -std=c++11");
          output.println(
              "CMD [\"./" + FilenameUtils.removeExtension(lambdaFileName) + "\", \"output\", \""
                  + newFileName + "\"]");
        }
      }
    } catch (Exception e) {
      log.error(String.format("An error has occurred :%s", e.getMessage()));
      throw new ProcessExecutionException(
          String.format("Dockerfile creation failed:%s", e.getMessage()));
    }
  }


  private void buildDockerImage(File dockerFolder) {
    Runtime runtime = Runtime.getRuntime();
    Process pro;
    try {
      pro = runtime.exec(
          "wsl docker build -t " + dockerFolder.getName().toLowerCase(Locale.ROOT) + " "
              + dockerProperties.getDirPathWsl() + dockerFolder.getName());
      printLines(" stdout:", pro.getInputStream());
      printLines(" stderr:", pro.getErrorStream());
      pro.waitFor();
      System.out.println(" exitValue() " + pro.exitValue());
    } catch (InterruptedException e) {
      log.error("InterruptedException: ", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.error(String.format("Docker image build failed:%s", e.getMessage()));
      throw new ProcessExecutionException(
          String.format("Docker image build failed:%s", e.getMessage()));
    }
  }

  private void runDockerContainer(File dockerFolder, String destDirPath, Integer lambdaId,
      Integer tenantId) {
    LambdaExecutionTimeEntity lambdaExecutionTimeEntity = new LambdaExecutionTimeEntity();

    TotalExecutionTimeEntity
        totalExecutionTimeEntity = totalExecutionTimeEntityRepository.findByTenantId(tenantId)
        .orElseThrow(() -> new NotFoundException("Cant' find total execution time table"));
    lambdaExecutionTimeEntity.setLambdaId(lambdaId);
    long startTime = System.currentTimeMillis();
    lambdaExecutionTimeEntity.setExecutionDate(new Timestamp(startTime));

    Runtime runtime = Runtime.getRuntime();
    Process pro;
    try {
      pro = runtime.exec(
          "wsl docker run --rm -v " + "/mnt/c/lambda-root/dest/" + lambdaId + ":/src/output "
              + dockerFolder.getName().toLowerCase(
              Locale.ROOT));
      printLines(" stdout:", pro.getInputStream());
      printLines(" stderr:", pro.getErrorStream());
      pro.waitFor();
      System.out.println(" exitValue() " + pro.exitValue());
    } catch (InterruptedException e) {
      log.error("InterruptedException: ", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.error(String.format("Docker image run failed:%s", e.getMessage()));
      throw new ProcessExecutionException(
          String.format("Docker image run failed:%s", e.getMessage()));
    }
    long endTime = System.currentTimeMillis();
    lambdaExecutionTimeEntity.setDuration((int) (endTime - startTime));
    lambdaExecutionTimeEntityRepository.save(lambdaExecutionTimeEntity);
    totalExecutionTimeEntity.setDuration(
        totalExecutionTimeEntity.getDuration() + lambdaExecutionTimeEntity.getDuration());
    totalExecutionTimeEntityRepository.save(totalExecutionTimeEntity);
  }

  private void deleteDockerImage(String imgName) {
    Runtime runtime = Runtime.getRuntime();
    Process pro;
    try {
      pro = runtime.exec(
          "wsl docker rmi " + imgName);
      pro.waitFor();
    } catch (InterruptedException e) {
      log.error("InterruptedException: ", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.error(String.format("Docker image rmi failed:%s", e.getMessage()));
      throw new ProcessExecutionException(
          String.format("Docker image rmi failed:%s", e.getMessage()));
    }
  }

  public List<ActiveLambdaExecutionTimeResponse> getActiveExecutionTime(String filter,
      Integer tenantId) {
    List<ActiveLambdaExecutionTimeResponse> lambdaExecutionTimeResponses;
    Timestamp endTime = Timestamp.from(Instant.now());
    Timestamp startTime;

    switch (filter) {
      case "DAY" -> {
        startTime = Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = lambdaExecutionTimeEntityRepository.findAllBetweenDates(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, ActiveLambdaExecutionTimeResponse.class))
            .toList();

      }
      case "WEEK" -> {
        startTime = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = lambdaExecutionTimeEntityRepository.findAllBetweenDates(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, ActiveLambdaExecutionTimeResponse.class))
            .toList();
      }

      case "MONTH" -> {
        startTime = Timestamp.from(Instant.now().minus(30, ChronoUnit.DAYS));
        lambdaExecutionTimeResponses = lambdaExecutionTimeEntityRepository.findAllBetweenDates(
                startTime, endTime, tenantId).stream()
            .map(pair -> modelMapper.map(pair, ActiveLambdaExecutionTimeResponse.class))
            .toList();
      }
      default -> throw new NotFoundException("Interval not supported");

    }

    return lambdaExecutionTimeResponses;
  }

  public void createTotalExecutionTime(Integer tenantId) {
    if (totalExecutionTimeEntityRepository.existsByTenantId(tenantId)) {
      throw new ConflictException("Tenant already exists");
    }
    TotalExecutionTimeEntity totalExecutionTimeEntity = new TotalExecutionTimeEntity();
    totalExecutionTimeEntity.setDuration(0);
    totalExecutionTimeEntity.setTenantId(tenantId);
    totalExecutionTimeEntity.setPeriodStart(new Date(System.currentTimeMillis()));
    totalExecutionTimeEntity.setMaxDuration(3600000);
    totalExecutionTimeEntityRepository.save(totalExecutionTimeEntity);
  }

  public void setUnlimitedDurationTime(Integer tenantId) {
    TotalExecutionTimeEntity totalExecutionTimeEntity = totalExecutionTimeEntityRepository.findByTenantId(
        tenantId).orElseThrow(() -> new NotFoundException("Tenant id not valid"));
    totalExecutionTimeEntity.setMaxDuration(null);
    totalExecutionTimeEntity.setPeriodStart(new Date(System.currentTimeMillis()));
    totalExecutionTimeEntityRepository.save(totalExecutionTimeEntity);
  }

  public void disableLambdasByDirPath(String dirPath) {
    lambdaEntityRepository.findAllBySrcPathOrDestPath(dirPath, dirPath).forEach(lambdaEntity -> {
      lambdaEntity.setIsEnabled(false);
      if (lambdaEntity.getSrcPath().equals(dirPath)) {
        lambdaEntity.setSrcPath("");
      } else {
        lambdaEntity.setDestPath("");
      }
      lambdaEntityRepository.save(lambdaEntity);
    });
  }

  public void changeFolder(ChangeLambdaFolderRequest changeLambdaFolderRequest) {
    lambdaEntityRepository.findAllBySrcPath(changeLambdaFolderRequest.getOldPath())
        .forEach(lambdaEntity -> {
          lambdaEntity.setSrcPath(changeLambdaFolderRequest.getNewPath());
          lambdaEntityRepository.save(lambdaEntity);
        });
    lambdaEntityRepository.findAllByDestPath(changeLambdaFolderRequest.getOldPath())
        .forEach(lambdaEntity -> {
          lambdaEntity.setDestPath(changeLambdaFolderRequest.getNewPath());
          lambdaEntityRepository.save(lambdaEntity);
        });
  }

  private static void printLines(String cmd, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(
        new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
      System.out.println(cmd + " " + line);
    }
  }
}
