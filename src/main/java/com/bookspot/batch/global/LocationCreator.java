package com.bookspot.batch.global;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class LocationCreator {
    private final GeometryFactory geometryFactory;

    public LocationCreator() {
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    public Point create(double longitude, double latitude) {
        return geometryFactory.createPoint(
                new Coordinate(longitude, latitude));
    }
}
