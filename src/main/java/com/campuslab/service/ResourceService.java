package com.campuslab.service;

import com.campuslab.enums.ResourceStatus;
import com.campuslab.enums.ResourceType;
import com.campuslab.exception.CampusLabException;
import com.campuslab.model.ComputerResource;
import com.campuslab.model.Equipment;
import com.campuslab.model.LabResource;
import com.campuslab.repository.LabRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceService {
    private final LabRepository repository;
    private final Map<String, LabResource> cache = new HashMap<>();

    public ResourceService(LabRepository repository) {
        this.repository = repository;
    }

    public List<LabResource> getAllResources() throws SQLException {
        List<LabResource> resources = repository.findAllResources();
        cache.clear();
        for (LabResource resource : resources) {
            cache.put(resource.getResourceId(), resource);
        }
        return resources;
    }

    public List<LabResource> getAvailableResources() throws SQLException {
        List<LabResource> available = new ArrayList<>();
        for (LabResource resource : getAllResources()) {
            if (resource.isAvailable()) {
                available.add(resource);
            }
        }
        return available;
    }

    public void addResource(String id, String name, ResourceType type, String location, int quantity)
            throws SQLException, CampusLabException {
        if (id.isBlank() || name.isBlank() || location.isBlank()) {
            throw new CampusLabException("Resource fields cannot be empty.");
        }
        if (quantity <= 0) {
            throw new CampusLabException("Quantity must be greater than zero.");
        }
        if (repository.resourceExists(id)) {
            throw new CampusLabException("Resource ID already exists.");
        }

        LabResource resource;
        if (type == ResourceType.COMPUTER) {
            resource = new ComputerResource(id, name, location, quantity, "Windows/Linux");
        } else {
            resource = new Equipment(id, name, location, quantity,
                    type == ResourceType.EXPERIMENT_KIT ? "Experiment Kit" : "General Equipment");
        }

        repository.addResource(resource, type);
    }

    public void updateStatus(String resourceId, ResourceStatus status) throws SQLException, CampusLabException {
        if (!repository.resourceExists(resourceId)) {
            throw new CampusLabException("Resource ID was not found.");
        }
        repository.updateResourceStatus(resourceId, status);
    }
}
