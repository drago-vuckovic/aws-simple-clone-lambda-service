package co.vuckovic.lambdaservice.api.v1.controller;

import co.vuckovic.model.dto.DummyPair;
import co.vuckovic.service.DummyService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class DummyController {

  private final DummyService dummyService;

  @PostMapping
  public ResponseEntity<Void> populateDB(@RequestBody List<DummyPair> dummies) {
    dummyService.populateDB(dummies);
    return ResponseEntity.ok().build();
  }
}
