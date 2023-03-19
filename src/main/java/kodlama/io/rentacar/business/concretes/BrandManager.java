package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.BrandService;
import kodlama.io.rentacar.entities.concretes.Brand;
import kodlama.io.rentacar.repository.abstracts.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandManager implements BrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandManager(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAll() {
        if (brandRepository.getAll().size() == 0) throw new RuntimeException("marka bulunamadı");
        return brandRepository.getAll();


    }

    @Override
    public Brand getById(int id) {
        return brandRepository.getById(id);
    }

    @Override
    public Brand add(Brand brand) {
        validateBrandName(brand);
        return brandRepository.add(brand);
    }


    @Override
    public Brand update(int id, Brand brand) {
        validateBrandName(brand);
        return brandRepository.update(id, brand);
    }

    @Override
    public void delete(int id) {
        brandRepository.delete(id);
    }

    private void validateBrandName(Brand brand) {
        if (brand.getName().length() == 0 || brand.getName().length() > 15)
            throw new IllegalArgumentException("length must be between zero and 15");
    }

}
