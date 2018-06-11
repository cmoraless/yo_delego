package kiwigroup.yodelego;

import java.util.List;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

public interface OnApplicationUpdateListener {
    void onApplicationsResponse(List<Offer> applications);
    void onApplicationError(String error);
}