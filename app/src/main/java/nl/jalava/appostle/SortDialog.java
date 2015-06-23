/*
 * Copyright (C) 2013-2015 Jeffrey Rusterholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.jalava.appostle;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by Jeffrey on 20-6-2015.
 */
public class SortDialog extends DialogFragment {
    private int mSort;
    private int mDir;

    public interface SortDialogListener {
        void onFinishSortDialog(int sort, int dir);
    }

    public SortDialog() {
        //
    }


    static SortDialog newInstance(int sort, int dir) {
        SortDialog f = new SortDialog();

        Bundle args = new Bundle();
        args.putInt("sort", sort);
        args.putInt("dir", dir);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSort = getArguments().getInt("sort");
        mDir = getArguments().getInt("dir");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sort_dialog, container, false);

        getDialog().setTitle(getString(R.string.sort_title));

        // Alphabetically or by name.
        final Spinner spinner_types = (Spinner) v.findViewById(R.id.sort_type_spinner);
        ArrayAdapter<CharSequence> adapter_types = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_types, android.R.layout.simple_spinner_item);
        adapter_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_types.setAdapter(adapter_types);
        spinner_types.setSelection(mSort);

        // Ascending or descending.
        final Spinner spinner_directions = (Spinner) v.findViewById(R.id.sort_direction_spinner);
        ArrayAdapter<CharSequence> adapter_directions = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_directions, android.R.layout.simple_spinner_item);
        adapter_directions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_directions.setAdapter(adapter_directions);
        spinner_directions.setSelection(mDir);

        Button OK_Button = (Button) v.findViewById(R.id.ok_button);
        OK_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSort = spinner_types.getSelectedItemPosition();
                mDir = spinner_directions.getSelectedItemPosition();
                SortDialogListener activity = (SortDialogListener) getActivity();
                activity.onFinishSortDialog(mSort, mDir);
                dismiss();
            }
        });

        Button Cancel_Button = (Button) v.findViewById(R.id.cancelButton);
        Cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
   }


}
