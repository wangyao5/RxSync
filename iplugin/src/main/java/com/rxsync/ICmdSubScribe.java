package com.rxsync;

import com.rxsync.annotations.Defer;
import com.rxsync.annotations.PathParam;
import com.rxsync.annotations.Path;
import com.rxsync.annotations.QueryParam;

@Defer("cmd://")
public interface ICmdSubScribe {

    @Path("www/{id}/{time}")
    void shutdown(@PathParam("id") String id, @PathParam("time") String time, @QueryParam("a") String a);

    @Path("www/{id}xxx/{time}")
    void ok(@PathParam("id") String id, @PathParam("time") String time, @QueryParam("a") String a);
}