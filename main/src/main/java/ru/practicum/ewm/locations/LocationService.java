package ru.practicum.ewm.locations;

import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.locations.dto.*;

import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.PI;

public interface LocationService {
    LocationDtoOutAdmin addLocation(LocationDtoIn locationDtoIn);

    LocationDtoOutAdmin getLocationByIdForAdmin(long locationId);

    LocationDtoOutPublic getLocationByIdForPublic(long locationId);

    List<LocationDtoOutShort> getLocations(int from, int size);

    LocationDtoOutAdmin updateLocation(long locationId, LocationDtoInUpdate locationDtoUpdate);

    void updateLocationsByEvent(Event event);

    void deleteLocation(long locationId);

    // Метод для вычисления расстояния между двумя географическими точками.
    // (lat1, lon1) - широта и долгота первой точки, (lat2, lon2) - соответственно второй.
    // Алгоритм взят из одноименной функции sql в файле data.sql.
    static float distance(float lat1, float lon1, float lat2, float lon2) {
        double radLat1;
        double radLat2;
        double theta;
        double radTheta;
        double distance = 0;

        if (Math.abs(lat1 - lat2) < 0.000001 && Math.abs(lon1 - lon2) < 0.000001) {
            return (float) distance;
        } else {
            // переводим градусы широты первой точки в радианы
            radLat1 = PI * lat1 / 180;
            // переводим градусы широты второй точки в радианы
            radLat2 = PI * lat2 / 180;
            // находим разность долгот
            theta = lon1 - lon2;
            // переводим градусы в радианы
            radTheta = PI * theta / 180;
            // находим длину ортодромии
            distance = sin(radLat1) * sin(radLat2) + cos(radLat1) * cos(radLat2) * cos(radTheta);

            if (distance > 1) {
                distance = 1;
            }

            distance = acos(distance);
            // переводим радианы в градусы
            distance = distance * 180 / PI;
            // переводим градусы в километры
            distance = distance * 60 * 1.8524;

            return (float) distance;
        }
    }
}
