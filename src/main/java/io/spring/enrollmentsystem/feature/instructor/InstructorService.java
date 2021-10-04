package io.spring.enrollmentsystem.feature.instructor;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import io.spring.enrollmentsystem.common.exception.ResourceNotFoundException;
import io.spring.enrollmentsystem.common.service.PatchService;
import io.spring.enrollmentsystem.common.service.SpecificationService;
import io.spring.enrollmentsystem.feature.authority.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

/**
 * (Instructor) service
 *
 * @author Khoale
 * @since 2021-09-02 (00:39:59)
 */
@Service
@Slf4j @RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;
    private final PatchService patchService;
    private final SpecificationService specificationService;

    @Transactional(readOnly = true)
    public Instructor getInstructorById(UUID instructorId) {
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
    }

    @Transactional(readOnly = true)
    public Instructor getInstructorWithListOfSection(UUID instructorId) {
        return instructorRepository.findWithListOfSectionById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
    }

    @Transactional(readOnly = true)
    public InstructorDto getInstructorDtoById(UUID instructorId) {
        return instructorRepository.findById(InstructorDto.class, instructorId)
                .orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
    }

    @Transactional(readOnly = true)
    public Page<InstructorDto> getInstructorDtoPageableByPredicate(MultiValueMap<String, String> parameters,
                                                                   Pageable pageable) {
        return instructorRepository
                .findAll(InstructorDto.class, specificationService.getSpecifications(parameters), pageable);
    }

    @Transactional
    public InstructorDto createInstructor(InstructorDto instructorDto) {
        Instructor transientInstructor = instructorMapper.toInstructor(instructorDto);
        return instructorMapper.toInstructorDto(instructorRepository.save(transientInstructor));
    }

    @Transactional
    public InstructorDto updateInstructor(UUID instructorId, InstructorDto instructorDto) {
        Instructor currentInstructor = getInstructorById(instructorId);

        return instructorMapper
                .toInstructorDto(instructorMapper.toExistingInstructor(instructorDto, currentInstructor));
    }

    @Transactional
    public InstructorDto patchInstructor(UUID instructorId, JsonMergePatch mergePatchDocument, Class<?> viewClass) {
        Instructor currentInstructor = getInstructorById(instructorId);
        InstructorDto currentInstructorDto = instructorMapper.toInstructorDto(currentInstructor);

        InstructorDto patchedInstructorDto = patchService.mergePatch(mergePatchDocument,
                                                                     currentInstructorDto,
                                                                     InstructorDto.class,
                                                                     viewClass);

        return instructorMapper
                .toInstructorDto(instructorMapper.toExistingInstructor(patchedInstructorDto, currentInstructor));
    }

    @Transactional
    public void deleteById(UUID instructorId) {
        if (instructorRepository.existsById(instructorId)) {
            instructorRepository.deleteById(instructorId);
        } else {
            throw new ResourceNotFoundException(Instructor.class, instructorId);
        }
    }

}