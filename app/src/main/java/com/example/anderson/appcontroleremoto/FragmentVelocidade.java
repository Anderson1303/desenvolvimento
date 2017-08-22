package com.example.anderson.appcontroleremoto;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by anderson on 22/07/17.
 */

public class FragmentVelocidade extends Fragment{

    /* Definição dos objetos que serão usados na Activity Principal
    statusMessage mostrará mensagens de status sobre a conexão
    counterMessage mostrará o valor do contador como recebido do Arduino
    connect é a thread de gerenciamento da conexão Bluetooth
 */
    static TextView statusMessage;
    ConnectionThread connect;
    Button btnFrente,btnTraz,btnDireita,btnEsquerda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        btnFrente = (Button) view.findViewById(R.id.btn_cima);
        btnTraz = (Button) view.findViewById(R.id.btn_baixo);
        btnDireita = (Button) view.findViewById(R.id.btn_direita);
        btnEsquerda = (Button) view.findViewById(R.id.btn_esquerda);

        /* Link entre os elementos da interface gráfica e suas
            representações em Java.
         */

        statusMessage = (TextView) view.findViewById(R.id.statusMessage);

        /* Teste rápido. O hardware Bluetooth do dispositivo Android
            está funcionando ou está bugado de forma misteriosa?
            Será que existe, pelo menos? Provavelmente existe.
         */

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Que pena! Hardware Bluetooth não está funcionando :(");
        } else {
            statusMessage.setText("Ótimo! Hardware Bluetooth está funcionando :)");
        }

        /* A chamada do seguinte método liga o Bluetooth no dispositivo Android
            sem pedido de autorização do usuário. É altamente não recomendado no
            Android Developers, mas, para simplificar este app, que é um demo,
            faremos isso. Na prática, em um app que vai ser usado por outras
            pessoas, não faça isso.
         */
        btAdapter.enable();

        /* Definição da thread de conexão como cliente.
            Aqui, você deve incluir o endereço MAC do seu módulo Bluetooth.
            O app iniciará e vai automaticamente buscar por esse endereço.
            Caso não encontre, dirá que houve um erro de conexão.
         */
        connect = new ConnectionThread("98:D3:31:B2:39:D2");
        connect.start();

        /* Um descanso rápido, para evitar bugs esquisitos.
         */

        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }

            /* Esse método é invocado sempre que o usuário clicar na TextView
        que contem o contador. O app Android transmite a string "restart",
        seguido de uma quebra de e linha, que é o indicador de fim de mensagem.
     */
        btnFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.write("f".getBytes());

            }
        });


        btnTraz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.write("t".getBytes());

            }
        });

        btnDireita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.write("d".getBytes());

            }
        });

        btnEsquerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.write("e".getBytes());

            }
        });

        return view;
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            /* Esse método é invocado na Activity principal
                sempre que a thread de conexão Bluetooth recebe
                uma mensagem.
             */
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            /* Aqui ocorre a decisão de ação, baseada na string
                recebida. Caso a string corresponda à uma das
                mensagens de status de conexão (iniciadas com --),
                atualizamos o status da conexão conforme o código.
             */
            if (dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if (dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
            else {

                /* Se a mensagem não for um código de status,
                    então ela deve ser tratada pelo aplicativo
                    como uma mensagem vinda diretamente do outro
                    lado da conexão. Nesse caso, simplesmente
                    atualizamos o valor contido no TextView do
                    contador.
                 */
            }
        }
    };
}