package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.model.Plain;
import com.agatapietrzycka.ticketreservation.repository.PlainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlainService {

    private final PlainRepository plainRepository;

    public List<Plain> getPlain(){
        return plainRepository.findAll();
    }
}
