package com.rxsync;

import com.rxsync.annotations.Defer;
import com.rxsync.annotations.Param;
import com.rxsync.annotations.Path;

@Defer("cmd://")
public interface ICmdSubScribe {

    @Path("www/{id}/{time}")
    void shutdown(@Param("id") String id, @Param("time") String time);
}