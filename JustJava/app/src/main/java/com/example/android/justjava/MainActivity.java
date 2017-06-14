package com.example.android.justjava;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import static android.R.attr.name;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.justjava.R.id.chocolate;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {


    int quantity = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        //add space to operator and equal sign to better understand code (convention/best practice)

        //figure out whether or not they want whipped cream
        CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.wipped_cream_checkbox);
        boolean hasWhippedCream = whippedCreamCheckBox.isChecked();

        //figure out whether or not they want chocolate
        CheckBox chocolateCheckBox = (CheckBox) findViewById(chocolate);
        boolean hasChocolate = chocolateCheckBox.isChecked();


        EditText nameField = (EditText) findViewById(R.id.name_field);
        String name = nameField.getText().toString();

        int price =  calculatePrice(hasWhippedCream, hasChocolate);
        String priceMessage = createOrderSummary(name, price, hasWhippedCream, hasChocolate);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Just Java order for " + name);
        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {//verify if can be handle by other activity
            startActivity(intent);
        }





    }

    /**
     * Calculates the price of the order.
     *@param addChocolate is there chocolate?
     * @param addWhippedCream is there whippedCream
     * @return the price
     */
    private int calculatePrice(boolean addWhippedCream, boolean addChocolate) {
        //price one cup of coffee
        int basePrice = 5;

        // add 1$ on coffee price
        if (addWhippedCream){
            basePrice = basePrice + 1;
        }

        //add 2$ on coffee price
        if (addChocolate){
            basePrice = basePrice + 2;

        }

        return quantity * basePrice;
    }

    /**
     * return a summary of the order
     * @param name of the customer
     * @param price of the order
     * @param addWhippedCream is whether or  not the user has whippedCream
     * @return a text summary
     */
    //instead of creating multiple variable I think it good practice to add to that variable(reuse them)
    private String createOrderSummary(String name, int price, boolean addWhippedCream, boolean addChocolate){
        //Kaptain Kunal was the initial name
        String priceMessage = getString(R.string.order_summary_name, name);
        priceMessage += "\n" + getString(R.string.order_summary_whipped_cream, addWhippedCream);
        priceMessage += "\nAdd chocolate? " + addChocolate;
        priceMessage += "\nQuantity: " + quantity;
        priceMessage += "\nTotal: $" + price;
        priceMessage += "\nThank You!";

        return priceMessage;
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }





    public void increment(View view) {

        if (quantity == 100){
            //Show an error message as a toast
            Toast.makeText(this,"You cannot have more than 100 coffees",Toast.LENGTH_SHORT).show();
            return;

        }
        quantity = quantity + 1;
        displayQuantity(quantity);

    }

    public void decrement(View view){

        //THIS CONCEPT IS VITAL TO ME !!!!
        if (quantity == 1){
            Toast.makeText(this, "you cannot have less than 1 coffee",Toast.LENGTH_SHORT).show();
            return;//skip the rest of code
        }
        quantity = quantity - 1;//this won't be executed if condition true (return)
        displayQuantity(quantity);

    }


}