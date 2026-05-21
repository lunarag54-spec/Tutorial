package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.common.exception.NotFoundException;
import com.ccsw.tutorial.loan.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;

    public ClientServiceImpl(ClientRepository clientRepository, LoanRepository loanRepository) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public Client get(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client create(ClientDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        String normalizedName = normalizeName(dto.getName());
        if (clientRepository.existsByName(normalizedName)) {
            throw new BadRequestException("CLIENT_NAME_EXISTS", "Ya existe un cliente con ese nombre.");
        }
        Client client = new Client();
        client.setName(normalizedName);
        return clientRepository.save(client);
    }

    @Override
    public Client update(Long id, ClientDto dto) {
        if (dto == null) {
            throw new BadRequestException("BAD_REQUEST", "El cuerpo de la petición es obligatorio.");
        }
        Client client = clientRepository.findById(id).orElseThrow(() -> new NotFoundException("CLIENT_NOT_FOUND", "No existe el cliente indicado."));
        String normalizedName = normalizeName(dto.getName());
        if (!normalizedName.equals(client.getName()) && clientRepository.existsByName(normalizedName)) {
            throw new BadRequestException("CLIENT_NAME_EXISTS", "Ya existe un cliente con ese nombre.");
        }
        client.setName(normalizedName);
        return clientRepository.save(client);
    }

    @Override
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException("CLIENT_NOT_FOUND", "No existe el cliente indicado.");
        }
        if (loanRepository.existsByClient_Id(id)) {
            throw new ConflictException("CLIENT_HAS_LOANS", "El cliente tiene préstamos asociados; no se puede eliminar.");
        }
        clientRepository.deleteById(id);
    }

    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("BAD_REQUEST", "El nombre del cliente es obligatorio.");
        }
        return name.trim();
    }
}
