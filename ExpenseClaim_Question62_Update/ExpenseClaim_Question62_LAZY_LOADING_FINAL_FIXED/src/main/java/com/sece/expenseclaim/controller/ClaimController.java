package com.sece.expenseclaim.controller;

import com.sece.expenseclaim.dto.*;
import com.sece.expenseclaim.entity.Claim;
import com.sece.expenseclaim.entity.ClaimStatus;
import com.sece.expenseclaim.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins="*")
public class ClaimController {
    private final ClaimService service;
    public ClaimController(ClaimService service){this.service=service;}

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ClaimSummary create(@Valid @RequestBody ClaimCreateRequest request){return service.createSummary(request);}

    @GetMapping("/{id}") public ClaimSummary get(@PathVariable Long id){return service.getSummary(id);}

    @GetMapping
    public Page<ClaimSummary> list(@RequestParam(required=false) ClaimStatus status,@RequestParam(required=false) Long employeeId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="submittedAt") String sortBy,@RequestParam(defaultValue="desc") String direction){
        String safeSort=switch(sortBy){case "totalAmount","title","status","submittedAt","claimNumber"->sortBy; default->"submittedAt";};
        Sort sort=direction.equalsIgnoreCase("asc")?Sort.by(safeSort).ascending():Sort.by(safeSort).descending();
        return service.page(status,employeeId,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),50),sort));
    }

    @GetMapping("/stage/{status}") public Page<ClaimSummary> byStage(@PathVariable ClaimStatus status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){return service.page(status,null,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),50),Sort.by("submittedAt").descending()));}
    @GetMapping("/employee/{employeeId}") public Page<ClaimSummary> byEmployee(@PathVariable Long employeeId,@RequestParam(required=false) ClaimStatus status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){return service.page(status,employeeId,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),50),Sort.by("submittedAt").descending()));}
    @PutMapping("/{id}/approve") public ClaimSummary approve(@PathVariable Long id,@Valid @RequestBody ManagerDecisionRequest request){return service.decideSummary(id,request);}
    @PostMapping("/{id}/approve") public ClaimSummary approvePost(@PathVariable Long id,@Valid @RequestBody ManagerDecisionRequest request){return service.decideSummary(id,request);}
    @PutMapping("/{id}/reject") public ClaimSummary reject(@PathVariable Long id,@Valid @RequestBody ManagerDecisionRequest request){return service.rejectSummary(id,request);}
    @PostMapping("/{id}/reject") public ClaimSummary rejectPost(@PathVariable Long id,@Valid @RequestBody ManagerDecisionRequest request){return service.rejectSummary(id,request);}
    @PutMapping("/{id}/pay") public ClaimSummary pay(@PathVariable Long id,@Valid @RequestBody PaymentRequest request){return service.paySummary(id,request);}
    @GetMapping("/dashboard") public DashboardResponse dashboard(){return service.dashboard();}
}
