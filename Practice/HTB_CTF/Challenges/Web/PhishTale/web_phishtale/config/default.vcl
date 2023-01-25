vcl 4.1;

backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

acl admin {
 "127.0.0.1";
}

sub vcl_recv {
    set req.backend_hint = default;
    if ( req.url ~ "^/admin/export" && !(client.ip ~ admin) ) {
        return(synth(403, "Only localhost is allowed."));
    }
}