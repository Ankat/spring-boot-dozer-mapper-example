package com.spring.crud.demo.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.crud.demo.dto.SuperHeroDTO;
import com.spring.crud.demo.exception.InternalServerErrorException;
import com.spring.crud.demo.exception.NotFoundException;
import com.spring.crud.demo.mapper.SuperHeroMapper;
import com.spring.crud.demo.model.SuperHero;
import com.spring.crud.demo.service.ISuperHeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/super-heroes")
@RestController(value = "superHeroController")
public class SuperHeroController {

    private final ISuperHeroService superHeroService;
    private final SuperHeroMapper superHeroMapper;
    private final ObjectMapper objectMapper;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SuperHeroDTO>> findAllSuperHeros() {
        List<SuperHero> superHeroList = superHeroService.findAllSuperHeros();
        return ResponseEntity.ok().body(superHeroList.stream().map(superHero -> superHeroMapper.convertFromEntityToDto(superHero)).collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SuperHeroDTO> findSuperHeroById(@PathVariable int id) {
        try {
            return ResponseEntity.ok().body(superHeroMapper.convertFromEntityToDto(superHeroService.findSuperHeroById(id).get()));
        } catch (Exception ex) {
            throw new NotFoundException("No Super Hero found : " + id);
        }
    }

    @GetMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SuperHeroDTO>> findSuperHerosByExample(@RequestParam Map<String, Object> allRequestParams) {
        try {
            SuperHeroDTO superHeroDTO = objectMapper.convertValue(allRequestParams, SuperHeroDTO.class);
            List<SuperHero> superHeroList = superHeroService.findSuperHerosByExample(superHeroMapper.convertFromDtoToEntity(superHeroDTO));
            return ResponseEntity.status(HttpStatus.OK).body(superHeroList.stream().map(superHero -> superHeroMapper.convertFromEntityToDto(superHero)).collect(Collectors.toList()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Something went wrong");
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SuperHeroDTO> saveSuperHero(@RequestBody SuperHeroDTO superHeroDTO) {
        try {
            Optional<SuperHero> optionalSuperHero = superHeroService.saveSuperHero(superHeroMapper.convertFromDtoToEntity(superHeroDTO));
            URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/{id}")
                    .buildAndExpand(optionalSuperHero.get().getId())
                    .toUri();
            return ResponseEntity.created(uri).body(superHeroMapper.convertFromEntityToDto(optionalSuperHero.get()));
        } catch (Exception ex) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SuperHeroDTO> updateSuperHero(@PathVariable int id, @RequestBody SuperHeroDTO superHeroDTO) {
        try {
            Optional<SuperHero> optionalSuperHero = superHeroService.updateSuperHero(id, superHeroMapper.convertFromDtoToEntity(superHeroDTO));
            URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/{id}")
                    .buildAndExpand(optionalSuperHero.get().getId())
                    .toUri();
            return ResponseEntity.created(uri).body(superHeroMapper.convertFromEntityToDto(optionalSuperHero.get()));
        } catch (Exception ex) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Boolean> deleteSuperHero(@PathVariable int id) {
        try {
            return ResponseEntity.ok().body(superHeroService.deleteSuperHero(id));
        } catch (Exception ex) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }
}
