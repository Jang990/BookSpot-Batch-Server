package com.bookspot.batch.step.reader.api.library;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.global.openapi.ApiRequester;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryApiRequester {
    private final ApiRequester apiRequester;
    private final NaruApiUrlCreator naruApiUrlCreator;
    
    private static final Pageable ONLY_HEADER_PAGE = PageRequest.of(0, 1);

    public List<Library> findAllSupportedLibrary(Pageable pageable) {
        String url = naruApiUrlCreator.buildLibraryApi(pageable);
        return apiRequester.get(url, SupportedLibraryResponseSpec.class)
                .getResponse()
                .getLibs()
                .stream()
                .map(SupportedLibraryResponseSpec.Response.LibWrapper::getLib)
                .map(this::convert)
                .toList();
    }

    public int countSupportedLibrary() {
        String url = naruApiUrlCreator.buildLibraryApi(ONLY_HEADER_PAGE);
        return apiRequester.get(url, SupportedLibraryResponseSpec.class)
                .getResponse()
                .getNumFound();
    }

    private Library convert(SupportedLibraryResponseSpec.Response.LibraryInfoSpec lib) {
        return new Library(
                lib.getLibCode(),
                lib.getLibName(),
                lib.getAddress(),
                lib.getTel(),
                Double.parseDouble(lib.getLatitude()),
                Double.parseDouble(lib.getLongitude()),
                lib.getHomepage(),
                lib.getClosed(),
                lib.getOperatingTime()
        );
    }

}
