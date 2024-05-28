package com.example.tarefa5.api.ViaCEP;

import com.example.tarefa5.model.Aluno;
import com.example.tarefa5.model.ViaCEP;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCEPService {
    @GET("/ws/{cep}/json")
    Call<ViaCEP> getCep(@Path("cep") String cep);
}
