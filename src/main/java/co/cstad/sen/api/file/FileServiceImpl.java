// FileServiceImpl.java
package co.cstad.sen.api.file;

import co.cstad.sen.api.file.web.FileDto;
import co.cstad.sen.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    private final FileUtil fileUtil;

    @Autowired
    public FileServiceImpl(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public FileDto uploadSingle(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String extension = fileUtil.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileUtil.isExtensionAllowed(extension)) {
            throw new IllegalArgumentException("File extension is not valid");
        }

        if ("zip".equalsIgnoreCase(extension)) {
            return handleZipUpload(file);
        } else {
            return handleRegularFileUpload(file);
        }
    }

    private FileDto handleZipUpload(MultipartFile file) {
        FileDto zipFileDto = fileUtil.upload(file);
        List<FileDto> extractedFiles = fileUtil.extractZip(zipFileDto.getName(), "extracted_files_" + zipFileDto.getName());

        return FileDto.builder()
                .name(zipFileDto.getName())
                .extension(zipFileDto.getExtension())
                .size(zipFileDto.getSize())
                .url(zipFileDto.getUrl())
                .downloadUrl(zipFileDto.getDownloadUrl())
                .additionalInfo("Extracted files: " + extractedFiles.stream()
                        .map(fileDto -> fileDto.getName())
                        .collect(Collectors.joining(", ")))
                .build();
    }


    private FileDto handleRegularFileUpload(MultipartFile file) {
        return fileUtil.upload(file);
    }

    @Override
    public List<FileDto> uploadMultiple(List<MultipartFile> files) {
        List<FileDto> filesDto = new ArrayList<>();
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Files are empty");
        }
        if (files.size() > 20) {
            throw new IllegalArgumentException("File size is too large");
        }

        for (MultipartFile file : files) {
            String extension = fileUtil.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
            if (!fileUtil.isExtensionAllowed(extension)) {
                throw new IllegalArgumentException("File extension is not valid");
            }
            if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                FileDto fileDto = handleRegularFileUpload(file);
                filesDto.add(fileDto);
            } else if ("zip".equalsIgnoreCase(extension)) {
                FileDto fileDto = handleZipUpload(file);
                filesDto.add(fileDto);
            } else {
                FileDto fileDto = handleRegularFileUpload(file);
                filesDto.add(fileDto);
            }
        }
        return filesDto;
    }

    @Override
    public FileDto findByName(String name) throws IOException {
        Resource resource = fileUtil.load(name);
        if (resource.exists()) {
            return FileDto.builder()
                    .name(resource.getFilename())
                    .extension(fileUtil.getExtension(Objects.requireNonNull(resource.getFilename())))
                    .size(resource.contentLength())
                    .url(fileUtil.getUrl(resource.getFilename()))
                    .downloadUrl(fileUtil.getDownloadUrl(resource.getFilename()))
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    @Override
    public void delete(String name) {
        try {
            FileDto fileDto = findByName(name);
            if (fileDto != null) {
                fileUtil.delete(name);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Resource download(String name) throws IOException {
        return fileUtil.load(name);
    }
}