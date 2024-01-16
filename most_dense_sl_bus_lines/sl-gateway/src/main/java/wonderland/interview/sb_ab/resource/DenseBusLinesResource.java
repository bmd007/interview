package wonderland.interview.sb_ab.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import wonderland.interview.sb_ab.dto.BusLineDto;
import wonderland.interview.sb_ab.service.DenseBusLinesService;

@Slf4j
@RestController
@RequestMapping("/v1/bus/lines")
@CrossOrigin(origins = {"http://localhost", "http://localhost:3000"})
public class DenseBusLinesResource {

    private final DenseBusLinesService denseBusLinesService;

    public DenseBusLinesResource(DenseBusLinesService denseBusLinesService) {
        this.denseBusLinesService = denseBusLinesService;
    }

    @GetMapping(value = "most-dense/top10")
    public Flux<BusLineDto> mostDenseLines() {
        return denseBusLinesService.getTop10DenseBusLines();
    }

}
