package com.gymgate;

import java.util.Random;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class DatabaseFiller {
    /*
     * Used in testing to fill the database with random test data.
     * To be removed after development
     */
    private static final Logger logger = DbgLogger.getLogger();
    //This integer determines that how many of the test instances are created
    private static int QUANTITY = 30;
    private static LocalDateTime lastVisit = null;
    String[] finnishFirstNames = {
            "Aada", "Aamu", "Aapo", "Aatos", "Ahti", "Aida", "Aili", "Aino", "Akseli", "Alba",
            "Aleksi", "Aliisa", "Alina", "Allan", "Alma", "Alpo", "Amalia", "Amanda", "Anette", "Anni",
            "Anselmi", "Antero", "Antti", "Arja", "Armas", "Arne", "Arto", "Asta", "Aune", "Auvo",
            "Eemeli", "Eerika", "Eeva", "Eila", "Eino", "Eira", "Elias", "Elli", "Elma", "Elmeri",
            "Elviira", "Emma", "Enni", "Esa", "Essi", "Esteri", "Eveliina", "Hanna", "Hannu", "Harri",
            "Heidi", "Heikki", "Helena", "Helmi", "Henna", "Henrik", "Hilkka", "Hilla", "Hilma", "Hulda",
            "Iida", "Iina", "Iiris", "Iiro", "Ilkka", "Ilona", "Ilpo", "Inari", "Inka", "Into",
            "Irene", "Irma", "Isabella", "Ismael", "Ismo", "Isto", "Itä", "Jaakko", "Jani", "Janika",
            "Janne", "Jarmo", "Jatta", "Jere", "Jimi", "Joel", "Johanna", "Joonas", "Jorma", "Juhani",
            "Juhannus", "Juha-Pekka", "Juho", "Jukka", "Julia", "Julius", "Jussi", "Jyri", "Järvi", "Järvilehto",
            "Kai", "Kaisa", "Kalevi", "Kari", "Karoliina", "Kasper", "Kati", "Katja", "Katri", "Kauno",
            "Keijo", "Kerttu", "Kimi", "Kirsi", "Klaus", "Kosti", "Krista", "Kristian", "Kristiina", "Kukka"
    };

    String[] finnishLastNames = {
            "Aalto", "Ahde", "Ahola", "Ahonen", "Ahtisaari", "Ahto", "Airaksinen", "Ala-Fossi", "Alakoski", "Alanko",
            "Alatalo", "Alho", "Allén", "Alm", "Alén", "Alén-Savikko", "Anttonen", "Arajuuri", "Arnold", "Arola",
            "Aronen", "Asikainen", "Autio", "Berg", "Blomqvist", "Borg", "Borgström", "Bremer", "Brännback", "Collan",
            "Dunder", "Eerola", "Eklund", "Eklöf", "Elo", "Elonen", "Eloranta", "Elorinne", "Elotalo", "Elävä",
            "Engberg", "Enroth", "Erkkilä", "Erkki", "Erkkilä", "Eskelinen", "Eskola", "Eskolahti", "Estlander",
            "Fagerholm",
            "Finnberg", "Forsman", "Forsström", "Fränti", "Furuholm", "Färdig", "Gallen-Kallela", "Granholm",
            "Grönholm", "Haapakoski",
            "Haapanen", "Haavisto", "Hagelberg", "Hakala", "Hakamies", "Hakkarainen", "Hakkarinen-Kannisto", "Halkoaho",
            "Halme", "Halonen",
            "Halttunen", "Hannula", "Happonen", "Harju", "Hartikainen", "Hartman", "Hassinen", "Heikkilä", "Heikkinen",
            "Heino",
            "Heinonen", "Helminen", "Helve", "Hietala", "Hietanen", "Hilden", "Hiltunen", "Hirvonen",
            "Hirvonen-Laitinen", "Holappa",
            "Holm", "Holopainen", "Honkanen", "Hoppu", "Horsma", "Huhtala", "Huhtanen", "Huotari", "Hurskainen",
            "Husso",
            "Huttunen", "Hämäläinen", "Härkönen", "Häyrinen", "Hölttä", "Ihalainen", "Ikonen", "Ilvesmäki", "Inkinen",
            "Iso-Kuortti",
            "Itkonen", "Jaakkola", "Jalonen", "Janhonen", "Jansson", "Jokela", "Jokinen", "Jokipii", "Joutsen",
            "Juolahti"
    };

    String[] finnishAddresses = {
            "Ahvenistontie", "Ampujantie", "Ankkalammintie", "Arkkitehdinkatu", "Asemakatu", "Autotie",
            "Eläintarhantie", "Erottajankatu", "Etuniementie", "Fleminginkatu",
            "Fredrikinkatu", "Gummeruksenkatu", "Haapaniemenkatu", "Hakaniemenkatu", "Hannikaisenkatu",
            "Harjutorinkatu", "Helsinginkatu", "Herttoniemenkatu", "Hietalahdenkatu", "Hiihtäjäntie",
            "Hiihtomäentie", "Hiljankatu", "Hinkkalanmäentie", "Hitonhauta", "Hoitajantie", "Huopalahdentie",
            "Hämeentie", "Isonnevantie", "Iso Roobertinkatu", "Itämerenkatu",
            "Jaalankatu", "Jämsänkatu", "Jarrumiehenkatu", "Juurakkotie", "Jyväskylänkatu", "Järvikyläntie",
            "Kaarlenkatu", "Kaarontie", "Kaartintie", "Kahvelitie",
            "Kahvitauontie", "Kalliontie", "Kamppi Center", "Kangasalanväylä", "Kankurinkatu", "Kannaksentie",
            "Kantakaupunki", "Kapteeninkatu", "Kapteenintie", "Kapteeninviita",
            "Karhunkatu", "Katajanokanlaituri", "Kauppakatu", "Kauppiaskatu", "Keilaranta", "Keinulaudantie",
            "Keisarinviitta", "Kekkosentie", "Kelohonkatie", "Kellonsoittajankatu",
            "Keltavuokontie", "Kenraalinkatu", "Kettutie", "Kievarinkatu", "Kilo", "Kirkkokatu", "Kiskontie",
            "Kivikatu", "Kivikukkaronkatu", "Kivimiehentie",
            "Kivipelto", "Klaavuntie", "Koirasaarentie", "Kojonkatu", "Kolmihaarankatu", "Kolmipellonkatu",
            "Korkeavuorenkatu", "Koskelantie", "Koskenrannanpuisto", "Koskikatu",
            "Kotkankatu", "Kotkatie", "Kotkavuorenkatu", "Koukkuniementie", "Kouluvuorenkatu", "Kruunuvuorenkatu",
            "Kultakorventie", "Kumpulantie", "Kuninkaankatu", "Kuopiontie",
            "Kurkimäentie", "Kurvinen", "Kustaankatu", "Kutomotie", "Kyläsaarenkuja", "Käenkuja", "Käpylänkuja",
            "Kärpäsenkuja" };

    public DatabaseFiller() {
        logger.info("Created an instance of DatabaseFiller");
        fillwithUsers();
        fillwithCustomers(QUANTITY);
        fillWithEvents(QUANTITY);
    }

    public void fillwithUsers() {
        /*
         * Creates couple of other users than admin
         */
        CustomerDatabase.getInstance().registerUser("SaliMake", "make");
        CustomerDatabase.getInstance().registerUser("unkka", "unkka");
        CustomerDatabase.getInstance().registerUser("nyyssis", "kukaloijouluna");
        logger.info("Added additional users");
    }

    public void fillwithCustomers(int qty) {
        /*
         * Adds random customers with randomized membership types etc
         */
        Random random = new Random();
        logger.info("Adding test users for database, please wait....");
        for (int i = 0; i < qty; i++) {
            String firstName = finnishFirstNames[random.nextInt(finnishFirstNames.length)];
            String lastName = finnishLastNames[random.nextInt(finnishLastNames.length)];
            String email = firstName + "." + lastName + "@sahkoposti.fi";
            String phoneNumber = generatePhoneNumber();
            int addressNumber = random.nextInt(1000);
            String address = finnishAddresses[random.nextInt(finnishAddresses.length)] + " " + addressNumber;
            String mType = "";
            int coin = random.nextInt(2);
            if (coin == 1) {
                mType = "Kuukausijäsenyys";
                String[] dates = getRandomDates();

                CustomerDatabase.getInstance().addCustomerMonthly(firstName, lastName,
                        phoneNumber, email, mType, dates[0],
                        dates[1], address, 0, " ");
            } else {

                mType = "Kertakäynti";
                int visits = random.nextInt(30);

                CustomerDatabase.getInstance().addCustomerVisits(firstName, lastName,
                        phoneNumber, email, mType,
                        address, visits, " ");
            }

        }
        logger.info("Added " + qty + " test users in total.");
        logger.info("Customerdatabase filled");
    }

    public static String generatePhoneNumber() {
        /* 
         * Randomizes phone number to test customer
         */
        Random random = new Random();
        int[] firstParts = { 50, 40, 44, 45 };
        int firstPart = firstParts[random.nextInt(firstParts.length)];
        int secondPart = random.nextInt(9000000) + 1000000;
        return "0" + firstPart + secondPart;
    }

    public static String[] getRandomDates() {
        /*
         * Gets random dates for customers with monthly subscription with an idea that
         * date1
         * is actually random and date 2 just adds from 1 to 12 months over it to keep
         * database
         * a bit more consistent
         */
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);

        long randomDay1 = ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay());
        int months = ThreadLocalRandom.current().nextInt(1, 13);

        LocalDate date1 = LocalDate.ofEpochDay(randomDay1);
        LocalDate date2 = date1.plusMonths(months);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return new String[] { date1.format(formatter), date2.format(formatter) };
    }

    public void fillWithEvents(int qty) {
        /*
         * Adds random visits to event database
         */
        for (int i = 0; i < qty; i++) {
            String date = getRandomVisitDates();
            int cuId = getRandomCustomerId(qty);
            CustomerDatabase.getInstance().saveEvent(cuId, date);
            logger.info("Event " + i + " added");
        }
        logger.info("Added " + qty + " test events in total.");

    }

    public static String getRandomVisitDates() {
        /*
         * Gets random visit date in condition that the new date can't be older
         * than previous one
         */

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime random = null;
        do {
            long randomsecond = ThreadLocalRandom.current().nextLong(start.toEpochSecond(ZoneOffset.UTC),
                    end.toEpochSecond(ZoneOffset.UTC));
            random = LocalDateTime.ofEpochSecond(randomsecond, 0, ZoneOffset.UTC);
        } while (lastVisit != null && lastVisit.isBefore(random));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss");
        String visitDate = random.format(formatter);
        return visitDate;
    }

    public static int getRandomCustomerId(int qty) {
        //Gets randomized customer ID for events
        Random random = new Random();
        int cuId = random.nextInt(qty) + 1;
        return cuId;
    }
}
