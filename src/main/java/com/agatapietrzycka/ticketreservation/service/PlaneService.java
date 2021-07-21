package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.model.Plane;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaneService {

    private final PlaneRepository planeRepository;

    public List<Plane> getPlain(){
        return planeRepository.findAll();
    }
}
