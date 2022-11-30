package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestService testService;

    @PostMapping
    public TestEntity validityTest(@Valid @RequestBody TestEntity testEntity, BindingResult result) {
        if(result.hasErrors())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to accept new number");

        System.out.println("Saving entity");
        return testService.save(testEntity);
    }

    @PostMapping("${request.mapping}")
    public TestEntity funTest(@Valid @RequestBody TestEntity testEntity, BindingResult result) {
        if(result.hasErrors())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to accept new number");

        System.out.println("Saving entity");
        return testService.save(testEntity);

    }

    @GetMapping
    public List<TestEntity> showTest() {
        return testService.get();
    }
}
