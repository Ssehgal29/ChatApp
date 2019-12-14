package com.example.chatapp;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Friends");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        String arrayItem[]  = {"a","b","c","d","e",
        "f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w",
        "x","y","z"};

        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,arrayItem);
        listView.setAdapter(arrayAdapter);
        return view;
    }

}
