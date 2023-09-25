package co.vuckovic;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HelloWorldController {

  @GetMapping("users")
  public ResponseEntity<?> test() {
    return ResponseEntity.ok("Helllo World");
  }

}
