package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.dto.requests.create.CreateCarRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateCarRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateCarResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetAllCarsResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetCarResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateCarResponse;
import kodlama.io.rentacar.entities.Car;
import kodlama.io.rentacar.entities.CarStatusTypes;
import kodlama.io.rentacar.repository.CarRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarManager implements CarService {

    private final CarRepository repository;

    private final ModelMapper mapper;

    @Override
    public List<GetAllCarsResponse> getAll(boolean showCarsInMaintenance) {
        List<Car> cars = repository.findAll();
        return prepareCarResponse(cars, showCarsInMaintenance);
    }

    @Override
    public GetCarResponse getById(int id) {
        checkIfCarExistsById(id);
        Car car = repository.findById(id).orElseThrow();
        return mapper.map(car, GetCarResponse.class);
    }

    @Override
    public CreateCarResponse add(CreateCarRequest request) {
        Car car = mapper.map(request, Car.class);
        car.setId(0);
        repository.save(car);
        return mapper.map(car, CreateCarResponse.class);
    }

    @Override
    public UpdateCarResponse update(int id, UpdateCarRequest request) {
        checkIfCarExistsById(id);
        validateCarStatus(getById(id).getState(), request.getState());
        Car car = mapper.map(request, Car.class);
        car.setId(id);
        repository.save(car);
        return mapper.map(car, UpdateCarResponse.class);
    }

    @Override
    public void delete(int id) {
        checkIfCarExistsById(id);
        repository.deleteById(id);
    }

    // Business Rules
    private void checkIfCarExistsById(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Böyle bir araba mevcut değil.");
        }
    }

    private List<GetAllCarsResponse> prepareCarResponse(List<Car> cars, boolean showCarsInMaintenance) {
        if (showCarsInMaintenance) {
            return cars.stream()
                    .map(car -> mapper.map(car, GetAllCarsResponse.class))
                    .toList();
        }
        return cars.stream()
                .filter(car -> !car.getState().equals(CarStatusTypes.IN_MAINTENANCE))
                .map(car -> mapper.map(car, GetAllCarsResponse.class))
                .toList();
    }

    private void validateCarStatus(String beforeUpdate, String afterUpdate) {
        if (beforeUpdate.equals("IN_MAINTENANCE") && afterUpdate.equals("IN_MAINTENANCE")) {
            throw new IllegalArgumentException("Zaten bakımda olan araba bakıma gönderilememez.");
        }
        if (beforeUpdate.equals("RENTED") && afterUpdate.equals("IN_MAINTENANCE")) {
            throw new IllegalArgumentException("Kirada olan araba bakıma gönderilemez.");
        }
    }

}
