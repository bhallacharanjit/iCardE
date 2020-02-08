package com.aprosoftech.icarde;

class icardeSingleton {
    private static final icardeSingleton ourInstance = new icardeSingleton();

    static icardeSingleton getInstance() {
        return ourInstance;
    }


    public static final String baseurl = "http://apis.icarde.com/api/Icarde/";
    public static final String imageurl ="http://apis.icarde.com/Images/";
    public static final String RegisterUser = "RegisterUser";
    public static final String LoginUser = "LoginUser";
    public static final String AddBloodRequest = "AddBloodRequest";
    public static final String AddHelpUser = "AddHelpUser";

    public static final String ShowHelpUsers = "ShowHelpUsers";
    public static final String ShowUserlocation = "ShowUserlocation";
    public static final String MyHelpRequest = "MyHelpRequest";
    public static final String RemoveRequest = "RemoveRequest";
    public static final String AddBusiness = "AddBusiness";

    public static final String MyBusinesses = "MyBusinesses";
    public static final String AddFavBusiness = "AddFavBusiness";

    public static final String ShowFavBusiness = "ShowFavBusiness";
    public static final String RemoveFavBusiness = "RemoveFavBusiness";
    public static final String AddProduct = "AddProduct";
    public static final String AddProductImages = "AddProductImages";
    public static final String ShowProducts = "ShowProducts";
    public static final String AddOffer = "AddOffer";
    public static final String ShowBloodRequest = "ShowBloodRequest";




    private icardeSingleton() {
    }
}
