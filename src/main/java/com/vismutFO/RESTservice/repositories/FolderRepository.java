package com.vismutFO.RESTservice.repositories;

import com.vismutFO.RESTservice.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID>, JpaSpecificationExecutor<Folder> {
}
