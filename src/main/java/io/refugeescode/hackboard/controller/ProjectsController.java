package io.refugeescode.hackboard.controller;


import io.refugeescode.hackboard.domain.Project;
import io.refugeescode.hackboard.domain.User;
import io.refugeescode.hackboard.repository.ProjectRepository;
import io.refugeescode.hackboard.repository.UserRepository;
import io.refugeescode.hackboard.security.AuthoritiesConstants;
import io.refugeescode.hackboard.security.SecurityUtils;
import io.refugeescode.hackboard.service.dto.ProjectDto;
import io.refugeescode.hackboard.service.mapper.ProjectMapper;
import io.refugeescode.hackboard.web.api.controller.ProjectsApi;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.refugeescode.hackboard.web.rest.util.HeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.validation.Valid;

import static io.refugeescode.hackboard.security.AuthoritiesConstants.ADMIN;
import static io.refugeescode.hackboard.security.AuthoritiesConstants.USER;
import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@RestController
public class ProjectsController implements ProjectsApi {

    private ProjectRepository projectsRepository;
    private UserRepository userRepository;

    public ProjectsController(ProjectRepository projectsRepository, UserRepository userRepository) {

        this.projectsRepository = projectsRepository;
        this.userRepository = userRepository;
    }

    @Override
//    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Boolean> addProject(@RequestBody ProjectDto project) {
        if (!SecurityUtils.isCurrentUserInRole(ADMIN)) {
            return ResponseEntity.badRequest().header(String.valueOf(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not authenticated", "You need to be n admin to perform this action " ))).body(null);
        }
        Project entity = new Project();
        entity.setTitle(project.getTitle());
        entity.setDescription(project.getDescription());
        if (SecurityUtils.getCurrentUserLogin().isPresent()){
            String userlogin = SecurityUtils.getCurrentUserLogin().get();
            Optional<User> oneByLogin = userRepository.findOneByLogin(userlogin);
            if (oneByLogin.isPresent()){
                entity.setOwner(oneByLogin.get());
            }
        }
        projectsRepository.save(entity);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Override
//    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Boolean> editProject(@RequestBody ProjectDto project) {
        if (!SecurityUtils.isCurrentUserInRole(ADMIN) || !SecurityUtils.isCurrentUserInRole(USER)) {
            return ResponseEntity.badRequest().header(String.valueOf(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not authenticated", "You need to be n admin to perform this action " ))).body(null);
        }

        Project entity = projectsRepository.findOne(project.getId());
        entity.setTitle(project.getTitle());
        entity.setDescription(project.getDescription());
        projectsRepository.save(entity);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Override
   // @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ProjectDto>> listProjects() {
        if (!SecurityUtils.isCurrentUserInRole(ADMIN) || !SecurityUtils.isCurrentUserInRole(USER)) {
            return ResponseEntity.badRequest().header(String.valueOf(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not authenticated", "You need to be n admin to perform this action " ))).body(null);
        }
        return new ResponseEntity<>(
            projectsRepository.findAll().stream()
                .map(ProjectMapper.INSTANCE::projectToProjectDto)
                .collect(Collectors.toList()),
            HttpStatus.OK
        );
    }

    @Override
//    @Secured(AuthoritiesConstants.ANONYMOUS)
    public ResponseEntity<ProjectDto> viewProject(@PathVariable("projectId") Long projectId) {

        if (!SecurityUtils.isCurrentUserInRole(ADMIN) || !SecurityUtils.isCurrentUserInRole(USER)) {
            return ResponseEntity.badRequest().header(String.valueOf(HeaderUtil.createFailureAlert(ENTITY_NAME, "Not authenticated", "You need to be n admin to perform this action " ))).body(null);
        }
        return new ResponseEntity<>(
            ProjectMapper.INSTANCE.projectToProjectDto(
                projectsRepository.findOne(projectId)),
            HttpStatus.OK
        );
    }

    @Override
//    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Boolean> deleteProject(@PathVariable("projectId") Long projectId) {
            projectsRepository.delete(projectId);
            return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
