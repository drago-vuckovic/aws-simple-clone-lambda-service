package co.vuckovic.lambdaservice.api.v1.controller;

import co.vuckovic.model.request.ChangeLambdaFolderRequest;
import co.vuckovic.model.request.LambdaCheckRequest;
import co.vuckovic.model.request.LambdaServiceUpdateRequest;
import co.vuckovic.model.request.LambdaUpdateStatusRequest;
import co.vuckovic.model.response.ActiveLambdaExecutionTimeResponse;
import co.vuckovic.model.response.LambdaExecutionTimeResponse;
import co.vuckovic.model.response.LambdasResponse;
import co.vuckovic.request.LambdaServiceCreationRequest;
import co.vuckovic.service.LambdaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/lambda")
@RequiredArgsConstructor
public class LambdaController {

  private final ObjectMapper objectMapper;
  private final LambdaService lambdaService;

  @PostMapping("/create")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful request"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "Not found"),
          @ApiResponse(responseCode = "500", description = "Internal error")
      })
  public ResponseEntity<Integer> uploadLambdaFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("lambda") String lambdaServiceCreationJsonRequest)
      throws IOException, InterruptedException {

    LambdaServiceCreationRequest lambdaServiceCreationRequest =
        objectMapper.readValue(
            lambdaServiceCreationJsonRequest, LambdaServiceCreationRequest.class);

    return ResponseEntity.ok(lambdaService.processLambdaFile(file, lambdaServiceCreationRequest));
  }

  @PutMapping("/{id}")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful request"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "Not found"),
          @ApiResponse(responseCode = "500", description = "Internal error")
      })
  public ResponseEntity<Void> updateLambda(@PathVariable Integer id,
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam("lambda") String lambdaServiceUpdateRequestString)
      throws IOException, InterruptedException {
    LambdaServiceUpdateRequest lambdaServiceUpdateRequest = objectMapper.readValue(
        lambdaServiceUpdateRequestString, LambdaServiceUpdateRequest.class);
    lambdaService.updateLambda(id, file, lambdaServiceUpdateRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/check-if-exists")
  public Boolean checkAvailable(@Valid @RequestBody LambdaCheckRequest lambdaCheckRequest) {
    return (lambdaService.checkIfLambdaExists(lambdaCheckRequest));
  }

  @PostMapping("/disable-by-dir-path")
  public ResponseEntity<Void> disableByDirPath(@RequestBody String dirPath) {
    lambdaService.disableLambdasByDirPath(dirPath);
    return ResponseEntity.ok().build();
  }


  @GetMapping("/{tenantId}")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful request"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "Not found"),
          @ApiResponse(responseCode = "500", description = "Internal error")
      })
  public ResponseEntity<LambdasResponse> getAllLambdas(@PathVariable Integer tenantId) {
    return ResponseEntity.ok(lambdaService.getAllLambdas(tenantId));
  }

  @PutMapping("/{id}/status")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful request"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "Not found"),
          @ApiResponse(responseCode = "500", description = "Internal error")
      })
  public ResponseEntity<Void> updateLambdaStatus(@PathVariable Integer id,
      @RequestBody LambdaUpdateStatusRequest lambdaUpdateRequest) {
    lambdaService.updateLambdaStatus(id, lambdaUpdateRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful request"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "Not found"),
          @ApiResponse(responseCode = "500", description = "Internal error")
      })
  public ResponseEntity<Void> deleteLambda(@PathVariable Integer id) throws IOException {
    lambdaService.deleteLambda(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/execution-time/{filter}/{tenantId}")
  public ResponseEntity<List<LambdaExecutionTimeResponse>> getExecutionTimes(
      @PathVariable String filter, @PathVariable Integer tenantId) {
    return ResponseEntity.ok(lambdaService.getExecutionTimes(
        filter, tenantId));
  }

  @GetMapping("/active-execution-time/{filter}/{tenantId}")
  public ResponseEntity<List<ActiveLambdaExecutionTimeResponse>> getExecutionActiveTimes(
      @PathVariable String filter, @PathVariable Integer tenantId) {
    return ResponseEntity.ok(lambdaService.getActiveExecutionTime(
        filter, tenantId));
  }

  @PostMapping("/create-total-execution-time/{tenantId}")
  public ResponseEntity<Void> createTotalExecutionTime(@PathVariable Integer tenantId) {
    lambdaService.createTotalExecutionTime(tenantId);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/set-unlimited-duration/{tenantId}")
  public ResponseEntity<Void> setUnlimitedDuration(@PathVariable Integer tenantId) {
    lambdaService.setUnlimitedDurationTime(tenantId);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/change-folder")
  public ResponseEntity<Void> changeFolder(
      @RequestBody ChangeLambdaFolderRequest changeLambdaFolderRequest) {
    lambdaService.changeFolder(changeLambdaFolderRequest);
    return ResponseEntity.ok().build();
  }

}
