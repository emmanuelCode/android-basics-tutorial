package com.example.android.miwok;

import android.app.Activity;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * we override the array adapter
 */

public class WordAdapter extends ArrayAdapter<Word> {

    private int mBackgroundColor;


    public WordAdapter(Activity context, ArrayList<Word> word, int backgroundColor) {

        super(context,0, word);
        mBackgroundColor = backgroundColor;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link word} object located at this position in the list
        Word currentWord = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.miwok_text_view);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        miwokTextView.setText(currentWord.getMiwokTranslation());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
        // Get the version number from the current Word object and
        // set this text on the number TextView
        defaultTextView.setText(currentWord.getDefaultTranslation());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);



        if(currentWord.hasImage()){

            imageView.setImageResource(currentWord.getImageResourceID());

            //since were recycling views we need to make sure that the next one use is visible
            imageView.setVisibility(View.VISIBLE);

        }else{

            //if the activity has no image that set it to  gone so that it will be invisible and won't take up space
            imageView.setVisibility(View.GONE);

        }

        View linearLayout = listItemView.findViewById(R.id.list_item_color);

        int color = ContextCompat.getColor(getContext(), mBackgroundColor);
        
        linearLayout.setBackgroundColor(color);




        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
