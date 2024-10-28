package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.debtDetail.DebtDetailAddDto;
import people_service.dto.debtDetail.DebtDetailUpdateDto;
import people_service.service.DebtDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debt-detail")
public class DebtDetailController {

    private final DebtDetailService debtDetailService;

    public DebtDetailController(DebtDetailService debtDetailService) {
        this.debtDetailService = debtDetailService;
    }


    @PostMapping("/add/{id}")
    public ResponseEntity<Long> addDebtDetail(@PathVariable Long id, @RequestBody DebtDetailAddDto debtDetailAddDto) {
        Long rs = debtDetailService.addDebtDetail(id, debtDetailAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateDebtDetail(@PathVariable Long id, @RequestBody DebtDetailUpdateDto debtDetailUpdateDto) {
        Long rs = debtDetailService.updateDebtDetail(id, debtDetailUpdateDto);
        return ResponseEntity.ok().body(rs);
    }
}
