package kiwigroup.yodelego;

import java.util.List;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

public interface OnApplicationUpdateListener {
    void onApplicationsResponse(List<Application> applications);
    void onApplicationError(String error);
}