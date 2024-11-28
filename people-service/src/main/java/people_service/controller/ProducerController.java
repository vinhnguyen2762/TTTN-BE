package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.producer.ProducerAddDto;
import people_service.dto.producer.ProducerAdminDto;
import people_service.dto.producer.ProducerUpdateDto;
import people_service.service.ProducerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/producer")
public class ProducerController {

    private final ProducerService producerService;

    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ProducerAdminDto>> getAllProducerAdmin() {
        List<ProducerAdminDto> list = producerService.getAllProducerAdmin();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/get-all/{id}")
    public ResponseEntity<List<ProducerAdminDto>> getAllProducerSmallTrader(@PathVariable Long id) {
        List<ProducerAdminDto> list = producerService.getAllProducerSmallTrader(id);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addCustomer(@RequestBody ProducerAddDto producerAddDto) {
        Long rs = producerService.addProducer(producerAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateProducer(@PathVariable Long id, @RequestBody ProducerAddDto producerAddDto) {
        Long rs = producerService.updateProducer(id, producerAddDto);
        return ResponseEntity.ok().body(rs);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteProducer(@PathVariable Long id) {
        Long rs = producerService.deleteProducer(id);
        return ResponseEntity.ok().body(rs);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countProducerByStatusTrue() {
        Long rs = producerService.countProducerByStatusTrue();
        return ResponseEntity.ok().body(rs);
    }
}
