/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.alerts.transport.actions.service;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.master.TransportMasterNodeOperationAction;
import org.elasticsearch.alerts.AlertService;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 */
public class TransportAlertsServiceAction extends TransportMasterNodeOperationAction<AlertsServiceRequest, AlertsServiceResponse> {

    private final AlertService alertService;

    @Inject
    public TransportAlertsServiceAction(Settings settings, String actionName, TransportService transportService, ClusterService clusterService, ThreadPool threadPool, ActionFilters actionFilters, AlertService alertService) {
        super(settings, actionName, transportService, clusterService, threadPool, actionFilters);
        this.alertService = alertService;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.MANAGEMENT;
    }

    @Override
    protected AlertsServiceRequest newRequest() {
        return new AlertsServiceRequest();
    }

    @Override
    protected AlertsServiceResponse newResponse() {
        return new AlertsServiceResponse();
    }

    @Override
    protected void masterOperation(AlertsServiceRequest request, ClusterState state, ActionListener<AlertsServiceResponse> listener) throws ElasticsearchException {
        switch (request.getCommand()) {
            case "start":
                alertService.start();
                break;
            case "stop":
                alertService.stop();
                break;
            case "restart":
                alertService.start();
                alertService.stop();
                break;
            default:
                listener.onFailure(new ElasticsearchIllegalArgumentException("Command [" + request.getCommand() + "] is undefined"));
                return;
        }
        listener.onResponse(new AlertsServiceResponse(true));
    }

    @Override
    protected ClusterBlockException checkBlock(AlertsServiceRequest request, ClusterState state) {
        return state.blocks().globalBlockedException(ClusterBlockLevel.METADATA);
    }
}
