package at.modoo.service;

import at.modoo.core.hierarchy.HierarchicalFactory;
import at.modoo.entity.Code;
import at.modoo.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultCodeService implements CodeService {

    private final CodeRepository codeRepository;

    @Override
    public List<Code> get() {
        List<Code> codes = codeRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"));
        return HierarchicalFactory.build(codes);
    }

    @Override
    public List<Code> findByName(String name) {
        return codeRepository.findByName(name);
    }

}
