package sabina;

@FunctionalInterface interface ResultOnlyHandler<R> {
    R execute () throws Exception;
}

@FunctionalInterface interface RequestResultHandler<REQ, R> {
    R execute (REQ request) throws Exception;
}

@FunctionalInterface interface RequestResponseHandler<REQ, RES> {
    void execute (REQ request, RES response) throws Exception;
}

@FunctionalInterface public interface RequestResponseResultHandler<REQ, RES, R> {
    R execute (REQ request, RES response) throws Exception;
}
