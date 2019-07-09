// IHermesService.aidl
package com.ff.hermes;

// Declare any non-default types here with import statements
import com.ff.hermes.Response;
import com.ff.hermes.Request;

interface IHermesService {
    Response send(in Request request);
}
