package com.example.tarefa5.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tarefa5.R;
import com.example.tarefa5.api.ViaCEP.ViaCEPService;
import com.example.tarefa5.api.ViaCEP.ViaCepClient;
import com.example.tarefa5.api.aluno.AlunoService;
import com.example.tarefa5.api.aluno.AlunoClient;
import com.example.tarefa5.model.Aluno;
import com.example.tarefa5.model.ViaCEP;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoActivity extends AppCompatActivity {
    EditText txtCRa, txtCNome, txtCCep, txtCLogradouro, txtCComplemento, txtCBairro, txtCidade, txtUF;
    Button btnCadastrar;
    AlunoService alunoService;
    ViaCEPService viaCEPService;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnCadastrar = findViewById(R.id.btnCadastrar);
        id = getIntent().getIntExtra("id", 0);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        alunoService = AlunoClient.getAlunoService();
        viaCEPService = ViaCepClient.getCEPService();
        txtCRa = findViewById(R.id.txtCRa);
        txtCNome = findViewById(R.id.txtCNome);
        txtCCep = findViewById(R.id.txtCCep);
        txtCLogradouro = findViewById(R.id.txtCLogradouro);
        txtCComplemento = findViewById(R.id.txtCComplemento);
        txtCBairro = findViewById(R.id.txtCBairro);
        txtCidade = findViewById(R.id.txtCCidade);
        txtUF = findViewById(R.id.txtCUF);
        txtCCep.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                return;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtCCep.getText().toString().length() == 8 && txtCCep.getText().toString().charAt(5) != '-'){
                    Toast.makeText(AlunoActivity.this, "Buscando cep", Toast.LENGTH_SHORT).show();
                    viaCEPService.getCep(txtCCep.getText().toString()).enqueue(new Callback<ViaCEP>() {
                        @Override
                        public void onResponse(Call<ViaCEP> call, Response<ViaCEP> response) {
                            //04428-970
                            if (response.isSuccessful()) {
                                txtCCep.setText(response.body().getCep());
                                txtCComplemento.setText(response.body().getComplemento());
                                txtCLogradouro.setText(response.body().getLogradouro());
                                txtCBairro.setText(response.body().getBairro());
                                txtCidade.setText(response.body().getLocalidade());
                                txtUF.setText(response.body().getUf());
                            }
                        }

                        @Override
                        public void onFailure(Call<ViaCEP> call, Throwable t) {
                            Log.e("Obter cep", "Erro ao obter cep");
                        }
                    });
                }
            }
        });
        if (id > 0) {
            btnCadastrar.setText("Atualizar");
            alunoService.getAlunoPorId(id).enqueue(new Callback<Aluno>() {
                @Override
                public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                    if (response.isSuccessful()) {
                        Log.e("ID ALUNO", ""+ response.body());
                        txtCRa.setText(response.body().getRa()+"");
                        txtCNome.setText(response.body().getNome());
                        txtCCep.setText(response.body().getCep());
                        txtCComplemento.setText(response.body().getComplemento());
                        txtCLogradouro.setText(response.body().getLogradouro());
                        txtCBairro.setText(response.body().getBairro());
                        txtCidade.setText(response.body().getCidade());
                        txtUF.setText(response.body().getUf());
                    }
                }

                @Override
                public void onFailure(Call<Aluno> call, Throwable t) {
                    Log.e("Obter aluno", "Erro ao obter aluno");
                }
            });
        }

        btnCadastrar.setOnClickListener(e -> {
            Aluno aluno = new Aluno();
            aluno.setBairro(txtCBairro.getText().toString());
            aluno.setCep(txtCCep.getText().toString());
            aluno.setComplemento(txtCComplemento.getText().toString());
            aluno.setLogradouro(txtCLogradouro.getText().toString());
            aluno.setRa(Integer.parseInt(txtCRa.getText().toString()));
            aluno.setNome(txtCNome.getText().toString());
            aluno.setCidade(txtCidade.getText().toString());
            aluno.setUf(txtUF.getText().toString());
            aluno.setId(id);

            if(id == 0) inserirAluno(aluno);
            else {
                aluno.setId(id);
                EditarAluno(aluno);
            }
        });
    }
    private void inserirAluno(Aluno aluno) {
        Call<Aluno> call = alunoService.postAluno(aluno);
        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                if (response.isSuccessful()) {
                    // A solicitação foi bem-sucedida
                    Aluno createdPost = response.body();
                    Toast.makeText(AlunoActivity.this, "Inserido com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // A solicitação falhou
                    Log.e("Inserir", "Erro ao criar: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                // Ocorreu um erro ao fazer a solicitação
                Log.e("Inserir", "Erro ao criar: " + t.getMessage());
            }
        });
    }

    private void EditarAluno(Aluno aluno) {
        Call<Aluno> call = alunoService.putAluno(id, aluno);
        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                if (response.isSuccessful()) {
                    // A solicitação foi bem-sucedida
                    Aluno createdPost = response.body();
                    Toast.makeText(AlunoActivity.this, "Editado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // A solicitação falhou
                    Log.e("Inserir", "Erro ao criar: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                // Ocorreu um erro ao fazer a solicitação
                Log.e("Inserir", "Erro ao criar: " + t.getMessage());
            }
        });
    }
}