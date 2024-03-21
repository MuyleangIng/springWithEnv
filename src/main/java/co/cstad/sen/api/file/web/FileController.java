package co.cstad.sen.api.file.web;
import co.cstad.sen.base.BaseApi;
import co.cstad.sen.api.file.FileService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "405", content = @Content(schema = @Schema(hidden = true))),
})
@Tag(name = "Files")
public class FileController {
    private final FileService fileService;
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public BaseApi<?> uploadSingleFile(@RequestPart("file") MultipartFile file) {
        FileDto fileDto = fileService.uploadSingle(file);

        return BaseApi.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("File has been uploaded")
                .timeStamp(LocalDateTime.now())
                .data(fileDto)
                .build();
    }

    @PostMapping(value = "/uploads",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseApi<?> uploadMultiple(@RequestPart("files") List<MultipartFile> files) {
        log.info("Request file upload = {}", files);

        List<FileDto> filesDto = fileService.uploadMultiple(files);

        return BaseApi.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("Files have been uploaded")
                .timeStamp(LocalDateTime.now())
                .data(filesDto)
                .build();
    }
    @GetMapping("/{name}")
    public BaseApi<?> findByName(@PathVariable String name) throws IOException {

        FileDto fileDto = fileService.findByName(name);

        return BaseApi.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("File has been found")
                .timeStamp(LocalDateTime.now())
                .data(fileDto)
                .build();
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        fileService.delete(name);
    }

    @GetMapping("/download/{name}")

    public ResponseEntity<?> download(@PathVariable String name) throws IOException {
        Resource resource = fileService.download(name);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                        "attachment; filename=" + resource.getFilename())
                .body(resource);
    }
}