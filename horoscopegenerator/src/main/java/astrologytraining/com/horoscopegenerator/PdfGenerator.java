package astrologytraining.com.horoscopegenerator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class PdfGenerator extends PdfPageEventHelper {


    PdfWriter writer;
    Document document;

    String headerSuffix;
    String headerSuffixSecondLine;

    Map<String, byte[]> images;

    boolean dasaHasMoreTables = false;


    private String[][] dasaDetails;

    private static final String MEENA2_IMAGE = "MEENA2.JPG";


    BaseFont baseFont;


    public byte[] generatePdf(BirthChart birthChart, String filePath) {

        Planet.setExalDeli();

        dasaDetails = birthChart.getDasaDetails();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {

				/*Image image = Image.getInstance("guruji.jpg");
		        image.scaleAbsolute(34.0F, 34.0F);*/

            this.baseFont = BaseFont.createFont("Helvetica", "Cp1252", false);

            document = new Document(PageSize.A4.rotate(), 10.0F, 10.0F, 10.0F, 10.0F);

//				writer = PdfWriter.getInstance(document, fileStream);
            writer = PdfWriter.getInstance(document, byteStream);

            writer.setPageEvent(this);
            document.open();

            addMetaDataToPDF(birthChart.getNativeName());

            Paragraph paragraph2 = new Paragraph("", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK));
            // paragraph2.add(new Chunk(image,  0.0F, 0.0F));
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

            Paragraph paragraph1 = new Paragraph("Om Sri Veeraraghavaya Namaha", new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            paragraph1.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

            Paragraph paragraph3 = new Paragraph("  ", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK));
            paragraph3.setAlignment(Element.ALIGN_CENTER);
            paragraph3.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

//	          this.document.add(paragraph2);
            this.document.add(paragraph1);
            this.document.add(paragraph3);
            addEmptyLinesToPDF(50f);

            PdfPTable tableHeader = getHeader(birthChart);

            this.document.add(tableHeader);

            addEmptyLinesToPDF(10);

            PdfPTable table = new PdfPTable(2);

            table.setWidths(new int[]{300, 300});

            calculatePlanetStars(birthChart);

            PdfPTable rasiChart = addD1ChartToPDF(birthChart);

            PdfPTable navamsaChart = addD9ChartToPDF(birthChart);


            calculateDikbala(birthChart);

            calcJSAndGuna(birthChart);

//				PdfPTable planetTable = addPlanetTable(birthChart);

            PdfPTable houseTable = addHousesTable(birthChart);

            PdfPTable miscTable = addMiscTable(birthChart);

            PdfPTable miscTable2 = addMiscTable2(birthChart);

            table.addCell(rasiChart);
            table.addCell(navamsaChart);

            table.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);
            table.getRow(0).getCells()[1].setBorder(Rectangle.NO_BORDER);

            this.document.add(table);

            addEmptyLinesToPDF(1);

            PdfPTable section2 = new PdfPTable(1);

            section2.addCell(houseTable);

            section2.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);

            this.document.add(section2);

            PdfPTable section3 = new PdfPTable(1);


            section3.addCell(miscTable);

            section3.addCell(addRahuKetuRepTable(birthChart));

            section3.addCell(addDBA(birthChart));

            section3.addCell(addHelp());

            section3.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(1).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(2).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(3).getCells()[0].setBorder(Rectangle.NO_BORDER);
            addEmptyLinesToPDF(10);


            addEmptyLinesToPDF(1);

            this.document.add(section3);

            this.document.newPage();

            this.document.add(tableHeader);


            PdfPTable miscTables = new PdfPTable(1);

            dasaHasMoreTables = false;

            PdfPTable dasaTable = addDasaTableToPDF();

            miscTables.addCell(dasaTable);

            miscTables.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);

            this.document.add(miscTables);


            if (dasaHasMoreTables) {
                System.out.println("Dasa has more than one table");
                this.document.newPage();
                this.document.add(tableHeader);
            }


            String predictions = getPredictions(birthChart);

            PdfPTable predictionTable = new PdfPTable(1);

            predictionTable.addCell(new Phrase(predictions, new Font(this.baseFont, HoraStrings.EXTRA_MEDIUM_FONT_SIZE + 1, 0, BaseColor.BLACK)));

            this.document.add(predictionTable);


            this.document.close();
            this.writer.close();

            //fileStream should be used to send the file out to client.
        } catch (Exception ex) {
            ex.printStackTrace();
            this.document.close();
            this.writer.close();
            throw new RuntimeException(ex);
        }

        return byteStream.toByteArray();
    }

    public byte[] generateLocalPdf(BirthChart birthChart, String filePath , Map<String, byte[]> imageData) {

        Planet.setExalDeli();

        this.images = imageData;

        dasaDetails = birthChart.getDasaDetails();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {

            File file = new File(filePath);
            if (!file.isFile()) {
                file.createNewFile();
            }

            System.out.println("Created file " + filePath);
            FileOutputStream fileStream = new FileOutputStream(file);

//			Resources res = context.getResources();
//			Drawable drawable = res.getDrawable(R.drawable.guruji);
//			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//			byte[] bitMapData = stream.toByteArray();
//
//
//			Image image = Image.getInstance( bitMapData);
//	        image.scaleAbsolute(34.0F, 34.0F);

            this.baseFont = BaseFont.createFont("Helvetica", "Cp1252", false);

            document = new Document(PageSize.A4.rotate(), 10.0F, 10.0F, 10.0F, 10.0F);

            writer = PdfWriter.getInstance(document, fileStream);
//			writer = PdfWriter.getInstance(document, byteStream);

            writer.setPageEvent(this);
            document.open();

            addMetaDataToPDF(birthChart.getNativeName());

//           Paragraph paragraph2 = new Paragraph("", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK));

//          paragraph2.add(new Chunk(image,  0.0F, 0.0F));
//           paragraph2.setAlignment(Element.ALIGN_CENTER);
//          paragraph2.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

            Paragraph paragraph1 = new Paragraph("Om Sri Veeraraghavaya Namaha", new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            paragraph1.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

            Paragraph paragraph3 = new Paragraph("  ", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK));
            paragraph3.setAlignment(Element.ALIGN_CENTER);
            paragraph3.setFont(new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK));

//          this.document.add(paragraph2);
            this.document.add(paragraph1);
            this.document.add(paragraph3);
            addEmptyLinesToPDF(50f);

            PdfPTable tableHeader = getHeader(birthChart);

            this.document.add(tableHeader);

            addEmptyLinesToPDF(10);

            PdfPTable table = new PdfPTable(2);

            table.setWidths(new int[]{300, 300});

            calculatePlanetStars(birthChart);


            PdfPTable rasiChart = addD1ChartToPDF(birthChart);

            PdfPTable navamsaChart = addD9ChartToPDF(birthChart);


            calculateDikbala(birthChart);

            calcJSAndGuna(birthChart);

//			PdfPTable planetTable = addPlanetTable(birthChart);

            PdfPTable houseTable = addHousesTable(birthChart);

            PdfPTable miscTable = addMiscTable(birthChart);

            PdfPTable miscTable2 = addMiscTable2(birthChart);

            table.addCell(rasiChart);
            table.addCell(navamsaChart);

            table.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);
            table.getRow(0).getCells()[1].setBorder(Rectangle.NO_BORDER);

            this.document.add(table);

            addEmptyLinesToPDF(1);

            PdfPTable section2 = new PdfPTable(1);

            section2.addCell(houseTable);

            section2.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);

            this.document.add(section2);

            PdfPTable section3 = new PdfPTable(1);


            section3.addCell(miscTable);

            section3.addCell(addRahuKetuRepTable(birthChart));

            section3.addCell(addDBA(birthChart));

            section3.addCell(addHelp());

            section3.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(1).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(2).getCells()[0].setBorder(Rectangle.NO_BORDER);
            section3.getRow(3).getCells()[0].setBorder(Rectangle.NO_BORDER);
            addEmptyLinesToPDF(10);


            addEmptyLinesToPDF(1);

            this.document.add(section3);

            this.document.newPage();

            this.document.add(tableHeader);


            PdfPTable miscTables = new PdfPTable(1);

            dasaHasMoreTables = false;

            PdfPTable dasaTable = addDasaTableToPDF();

            miscTables.addCell(dasaTable);

            miscTables.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);

            this.document.add(miscTables);


            if (dasaHasMoreTables) {
                System.out.println("Dasa has more than one table");
                this.document.newPage();
                this.document.add(tableHeader);
            }


            String predictions = getPredictions(birthChart);

            PdfPTable predictionTable = new PdfPTable(1);

            predictionTable.addCell(new Phrase(predictions, new Font(this.baseFont, HoraStrings.EXTRA_MEDIUM_FONT_SIZE + 1, 0, BaseColor.BLACK)));

            this.document.add(predictionTable);


            String jands = getJandS(birthChart);

            PdfPTable jandsTable = new PdfPTable(1);

            jandsTable.addCell(new Phrase(jands, new Font(this.baseFont, HoraStrings.EXTRA_MEDIUM_FONT_SIZE + 1, 0, BaseColor.BLACK)));

            //TODO: This will print how j and s are determined.
//            this.document.add(jandsTable);



            this.document.close();
			this.writer.close();
			
			//fileStream should be used to send the file out to client.
	    }
	    catch (Exception ex) {
	    	ex.printStackTrace();
	        this.document.close();
	        this.writer.close();
	        throw new RuntimeException( ex); 
	    } 
		
		return byteStream.toByteArray();
 	}

    private String getJandS(BirthChart birthChart) {

        StringBuilder sb = new StringBuilder();

        sb.append("Determining Jeeva and Sareera for planets:");

        for (Planet planet : Planet.values()) {

            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == planet) {
                    currentPlanet = planetInfo;
                    break;
                }
            }

            if(currentPlanet.getJeevaExplanation() != null) {
                sb.append("\n\n").append(planet.toString()).append(": ");
                sb.append(currentPlanet.getJeevaExplanation()).append(currentPlanet.getSareeraExplanation()).append("\n");

            }
        }


        sb.append("\n\n\nDetermining Jeeva and Sareera for houses:");

        int i=0;
        Set<Integer> finishedHouses = new HashSet<Integer>(12);
        for(House house: birthChart.getRaasiChakra().getOrderedHouses()){

            int houseNo = house.getAssignedHouseNo();
            String otherHouseName = null;

            if (!finishedHouses.contains(houseNo)) {

                int otherHouse = getOtherHouse(birthChart.getRaasiChakra().getOrderedHouses(),  house.getRaasi()  );

                if(otherHouse != -1) {
                    for(House tempHouse: birthChart.getRaasiChakra().getOrderedHouses()){

                        if(tempHouse.getAssignedHouseNo() == otherHouse) {



                        }
                    }

                }

                String houseNumStr = houseNo + (otherHouse == -1 ? "" : "," + otherHouse);

                sb.append("\n\nHouse ").append(houseNumStr).append(": ");
                sb.append("House lord is ").append(house.getRaasi().getRaasiLord().toString()).append(". ");

                Planet planet = house.getRaasi().getRaasiLord();

                PlanetInfo currentPlanet = null;
                for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                    if (planetInfo.getPlanet() == planet) {
                        currentPlanet = planetInfo;
                        break;
                    }
                }

                if(currentPlanet.getJeevaExplanation() != null) {

                    sb.append(currentPlanet.getJeevaExplanation()).append(currentPlanet.getSareeraExplanation()).append("\n");

                }

                finishedHouses.add(houseNo);
                finishedHouses.add(otherHouse);
            }

        }

        sb.append("\n\n\n");

        return sb.toString();
    }

    private PdfPTable getHeader(BirthChart birthChart) throws DocumentException{
	     	
		   	 PdfPTable table = new PdfPTable(6);
		   	 
		   	 String[ ] headers = {"Name", "Date Of Birth", "Place Of Birth" , "Star" , "Thidhi", "Day" };
		   	    
		   	 table.setWidths(new int[]{ 25,20,35,15, 15, 4});
		   
		   	 for(String header: headers) {
		   		 PdfPCell cell =  getCell(header, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE );
		   		 cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		   		 table.addCell(cell);
		   	 }
  	   
		     PdfPCell nameCell = new PdfPCell();
		     nameCell.setFixedHeight(15.0F);
		     nameCell.addElement(new Phrase( birthChart.getNativeName() , new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
		     
	 		 table.addCell( getCell( birthChart.getNativeName(), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
	 		 
	 		 PdfPCell dobCell = new PdfPCell();
	 		 dobCell.setFixedHeight(15.0F);
	 		 dobCell.addElement(new Phrase(  birthChart.getBirthDetails() , new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
	 		 table.addCell( getCell( birthChart.getBirthDetails() , 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
	 		   
	 		 
	 		 PdfPCell pobCell = new PdfPCell();
	 		 pobCell.setFixedHeight(15.0F);
	 		 pobCell.addElement(new Phrase( birthChart.getPlaceOfBirth(), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
	 		 
	 		 table.addCell( getCell( birthChart.getPlaceOfBirth(), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
	  
	 		 PdfPCell starCell = new PdfPCell();
	 		 starCell.setFixedHeight(15.0F);
	 		 starCell.addElement(new Phrase( getInitCap( birthChart.getNakshatra()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
	 		 
	 		 table.addCell( getCell(  getInitCap( birthChart.getNakshatra() + "-" + birthChart.getPada()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
	 		 
	 		 
	 		 PdfPCell thidhiCell = new PdfPCell();
	 		 thidhiCell.setFixedHeight(15.0F);
	 		 thidhiCell.addElement(new Phrase( birthChart.getTithi() , new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
	 		 table.addCell( getCell(  getInitCap(birthChart.getTithi()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
	 		  
	 		 
	 		 PdfPCell dayCell = new PdfPCell();
	 		 dayCell.setFixedHeight(15.0F);
	 		 dayCell.addElement(new Phrase( birthChart.getDay() , new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
	 		 
	 		 table.addCell( getCell(  getInitCap(birthChart.getDay().substring(0,3)), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ));
 			 			 		    	
	  		return table;
	  	  
	}
		


    private void calculatePlanetStars(BirthChart birthChart) {

        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            double longitude = (planetInfo.getHouse().getRaasi().getRaasiNo() * 30 + planetInfo.longitude);

            int nakshatra = ((int) Math.floor(longitude / 13.333333333333334D));
            double balanceOfNakshatra = (1.0D - (longitude / 13.333333333333334D - nakshatra));
            int pada = ((int) Math.floor((1.0D - balanceOfNakshatra) / 0.25D) + 1);
            String starName = HoraStrings.NAKSHATRAS[nakshatra];

            Star star = Star.valueOf(starName);
            planetInfo.setPlanetStar(star);
            planetInfo.setPada(pada);

            String ud = "";
            if (star.getUd1() == pada || star.getUd2() == pada) {
                ud = "UD";
            } else if (star.getvUd1() == pada || star.getvUd2() == pada) {
                ud = "VUD";
            }

            if (planetInfo.getPlanet() != Planet.LAGNA) {
                planetInfo.setUdStatus(ud);
            }

            Planet[] ykPlanets = birthChart.getRaasiChakra().getLagna().getRaasi().getYogaKarakas();

            for (Planet ykPlanet : ykPlanets) {
                if (ykPlanet == star.getStarLord()) {
                    planetInfo.setInYkStar(true);
                    break;
                }
            }
        }

    }

    private void calculateDikbala(BirthChart birthChart) {

        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {

            int position = 0;
            switch (planetInfo.getPlanet()) {

                case JUPITER:
                case MERCURY:
                    position = 1;
                    break;

                case MOON:
                case VENUS:
                    position = 4;
                    break;

                case SATURN:
                case RAHU:
                    position = 7;
                    break;

                case SUN:
                case MARS:
                case KETU:
                    position = 10;
                    break;
                default:
                    break;

            }
            if (position > 0) {
                boolean hasDikbala = determineDikbala(birthChart, planetInfo, position);
                planetInfo.setHasDikbala(hasDikbala);
            }
        }

    }

    private boolean determineDikbala(BirthChart birthChart, PlanetInfo planetInfo, int position) {

//		System.out.println("Trying to calculate for " + planetInfo.getPlanet() + " " + position);

        //Rule 1: is planet posited in given position
        if (planetInfo.getHouse().getAssignedHouseNo() == position) {
// 			System.out.println("Rule 1 passed");
            return true;
        }

        //Rule 2: is star lord posited in given position
        Planet starLord = planetInfo.getPlanetStar().getStarLord();
        for (PlanetInfo planetInfo2 : birthChart.getRaasiChakra().getOrderedHouses().get(position - 1).getPlanets()) {
            if (planetInfo2.getPlanet() == starLord) {
//  				System.out.println("Rule 2 passed");
                return true;
            }
        }

        //Rule 3: is star lord the lord of given position
        if (birthChart.getRaasiChakra().getOrderedHouses().get(position - 1).getRaasi().getRaasiLord() == starLord) {
//  			System.out.println("Rule 3 passed");
            return true;
        }


        //Rule 4: is planet posited in conjunction with lord of given position
        Planet positionHouseLord = birthChart.getRaasiChakra().getOrderedHouses().get(position - 1).getRaasi().getRaasiLord();

        if (positionHouseLord != planetInfo.getPlanet()) {
            for (PlanetInfo planetInfo2 : planetInfo.getHouse().getPlanets()) {
                if (planetInfo2.getPlanet() == positionHouseLord) {
//	  				System.out.println("Rule 4 passed");
                    return true;
                }
            }
        }

        //Rule 5: Is planet aspected by lord of given position
        if (positionHouseLord != planetInfo.getPlanet()) {
            for (PlanetInfo planetInfo2 : planetInfo.getHouse().getAspectedPlanets()) {
                if (planetInfo2.getPlanet() == positionHouseLord) {
//	  				System.out.println("Rule 5 passed");
                    return true;
                }
            }
        }

        //Rule 6: Is planet aspected by any planet residing in given position
        for (PlanetInfo planetInfo2 : birthChart.getRaasiChakra().getOrderedHouses().get(position - 1).getPlanets()) {
            for (PlanetInfo planetInfo3 : planetInfo.getHouse().getAspectedPlanets()) {
                if (planetInfo3.getPlanet() == planetInfo2.getPlanet()) {
                    //Rahu and Ketu aspecting each other does not yield digbala
                    if ((planetInfo.getPlanet() == Planet.RAHU && planetInfo2.getPlanet() == Planet.KETU) ||
                            (planetInfo.getPlanet() == Planet.KETU && planetInfo2.getPlanet() == Planet.RAHU)) {
                        continue;
                    } else {
                        return true;
                    }
//	  				System.out.println("Rule 6 passed");
                }
            }
        }
        return false;
    }

	private Planet[] getJeevaSareeraPlanets(BirthChart birthChart, Planet forPlanet, PlanetInfo currentPlanet) {

        StringBuilder sb = new StringBuilder();


		Planet[] jsPlanets = {null, null};
		Planet starLord = getStarLord(birthChart, forPlanet);
//        sb.append("\n\n").append(forPlanet.toString()).append(": ");
        sb.append("The planet ").append(forPlanet.toString()).append("'s starlord is ").append(starLord.toString()).append(". ");

        jsPlanets[0] = getJeevaOrSareeraPlanet(birthChart, starLord, sb);
	 	Planet jeevaPlanet = jsPlanets[0];
	 	sb.append("So, Jeeva planet is ").append( jeevaPlanet).append(". ");

        if(forPlanet != Planet.LAGNA) {
            currentPlanet.setJeevaExplanation(sb.toString());
        }
        sb = new StringBuilder();

        starLord = getStarLord(birthChart, jeevaPlanet);
        sb.append("\nThe Jeeva planet ").append(jeevaPlanet.toString()).append("'s starlord is ").append(starLord.toString()).append(". ");

	 	//If Jeeva planet is in its own star, consider Raasi lord to determine Sareera Planet 
  	    for(PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
 	    	if(planetInfo.getPlanet() == jeevaPlanet) {
 	    		if( planetInfo.getPlanetStar().getStarLord() == jeevaPlanet){
  	    			starLord = planetInfo.getHouse().getRaasi().getRaasiLord();
                    sb.append("Due to Jeeva planet is in its own star, we need to consider Raasi lord ").append(starLord.toString());
 	    			break;
 	    		}
 	    	}
 	    }
   		jsPlanets[1] = getJeevaOrSareeraPlanet(birthChart, starLord, sb);
   		
   		Planet houseLord = null;
   		
   		if(jsPlanets[0] == jsPlanets[1]) {
    		//If Jeeva planet and Sareera planets are same, consider Raasi lord to determine Sareera Planet 
   	  	    for(PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
   	 	    	if(planetInfo.getPlanet() == jeevaPlanet) {
   	 	    		starLord = planetInfo.getHouse().getRaasi().getRaasiLord();
   	 	    		houseLord =starLord;
                    sb.append("Due to Jeeva planet and Sareera planets are same, we need to consider Raasi lord ").append(starLord.toString()).append(" as star lord to determine Sareera planet").append(". ");

                    break;
    	 	    }
   	 	    }
   	  	    jsPlanets[1] = getJeevaOrSareeraPlanet(birthChart, starLord, sb);
   		}

   		if(jsPlanets[0] == jsPlanets[1] && jsPlanets[0] == Planet.SATURN && houseLord == Planet.MARS) {
            sb.append("Due to Jeeva planet and Sareera planets are same, and Jeeva planet is Saturn and house lord is Mars, we should consider Sareera as Mars. " );

            //If jeeva and sareera is Saturn, then we should consider raasi lord as sareera. This can only happen for Saturn in Anuradha.
   			jsPlanets[1] = Planet.MARS;
   		}
        sb.append("So, Sareera planet is ").append( jsPlanets[1]).append(". ");;
        if(forPlanet != Planet.LAGNA) {
            currentPlanet.setSareeraExplanation(sb.toString());
        }
 	 	return jsPlanets;
		
	}
	
	private Planet getStarLord(BirthChart birthChart, Planet forPlanet){

        PlanetInfo currentPlanet = null;
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if (planetInfo.getPlanet() == forPlanet) {
                currentPlanet = planetInfo;
                break;
            }
        }

        double longitude = (currentPlanet.getHouse().getRaasi().getRaasiNo() * 30 + currentPlanet.longitude);

        int nakshatra = ((int) Math.floor(longitude / 13.333333333333334D));
        String starName = HoraStrings.NAKSHATRAS[nakshatra];
        Star star = Star.valueOf(starName);
        Planet starLord = star.getStarLord(); //This is planet's star lord.

        return starLord;
    }


    private Planet getJeevaOrSareeraPlanet(BirthChart birthChart, Planet starLord, StringBuilder sb) {

//		System.out.println("Trying to find out js for planet " + forPlanet.getShortName());

        Planet jOrSPlanet = null;
        PlanetInfo currentPlanet = null;

        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if (planetInfo.getPlanet() == starLord) {
                currentPlanet = planetInfo;
                break;
            }
        }

        int planetCount = currentPlanet.getHouse().getPlanets().size();
        if (currentPlanet.getHouse() == birthChart.getRaasiChakra().getLagna()) {
            planetCount--;
        }

        if (planetCount == 1) {
            sb.append("Also the planet has not conjoined with any other planet. ");
            jOrSPlanet = starLord;
        } else {
            //When there are multiple planets, find strongest among them
            //Only for string
            sb.append("The planet is conjoined with ");
            for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                sb.append(planetInfo.getPlanet().toString()).append(",");
            }
            sb.deleteCharAt(sb.length()-1).append(". ");
            //Only for string

            //Check if saturn is one of the stars along with SL
            if (currentPlanet.getHouse().getRaasi() == Raasi.VRUSCHIKA) {
                for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                    if (planetInfo.getPlanet() == Planet.SATURN && planetInfo.getPlanetStar() == Star.ANURADHA) {
                        jOrSPlanet = Planet.SATURN;
                        sb.append("The planet is in Vruchika and Saturn is in Anuradha Star. ");
                        break;
                    }
                }
            }
            //If jeeva planet is still null
            if (jOrSPlanet == null) {

                //Check if any planet is in own house
                for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                    if (planetInfo.getPlanet() == currentPlanet.getHouse().getRaasi().getRaasiLord()) {
                        jOrSPlanet = planetInfo.getPlanet();
                        sb.append("The planet ").append(jOrSPlanet.toString()).append(" is in own house " )
                                .append(currentPlanet.getHouse().getRaasi().toString()).append(". ");
                        break;
                    }
                }


                if (jOrSPlanet == null) {
                    //Check if Rahu is in kumbha or Ketu is in Mesha. If so, they are the jeeva/sareera planets
                    for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                        if (planetInfo.getPlanet() == Planet.RAHU && currentPlanet.getHouse().getRaasi() == Raasi.KUMBHAM) {
                            jOrSPlanet = Planet.RAHU;
                            sb.append("The planet Rahu is in Kumbha. ");
                            break;
                        } else if (planetInfo.getPlanet() == Planet.KETU && currentPlanet.getHouse().getRaasi() == Raasi.MESHAM) {
                            jOrSPlanet = Planet.KETU;
                            sb.append("The planet Ketu is in Mesha. ");
                            break;
                        }
                    }
                }
            }

            if (jOrSPlanet == null) {
                //Check if any exalted planet in this house
                for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                    if (planetInfo.getPlanet().getExaltation() == currentPlanet.getHouse().getRaasi()) {
                        jOrSPlanet = planetInfo.getPlanet();
                        sb.append("The planet ").append(jOrSPlanet.toString()).append(" is exalted in ")
                                .append(currentPlanet.getHouse().getRaasi().toString()).append(". ");
                        break;
                    }
                }
            }


            if (jOrSPlanet == null) {
                //Check if any planet has digbala
                List<PlanetInfo> digbalaPlanets = new ArrayList<PlanetInfo>();

                for (PlanetInfo planetInfo : currentPlanet.getHouse().getPlanets()) {
                    if (planetInfo.isHasDikbala()) {
                        digbalaPlanets.add(planetInfo);
                        sb.append(planetInfo.getPlanet().toString()).append(",");
                    }
                }

                if (digbalaPlanets.size() == 1) {
                    sb.deleteCharAt(sb.length()-1).append(" has digbala. ");
                    jOrSPlanet = digbalaPlanets.get(0).getPlanet();
                } else if (digbalaPlanets.size() > 1) {
                    sb.deleteCharAt(sb.length()-1).append(" have digbala. ");
                    //More than one planet has digbala.
                    jOrSPlanet = getStrongPlanet(digbalaPlanets, sb);
                } else {
                    sb.append("No other rules have satisfied. ");
                    jOrSPlanet = starLord;
                    System.out.println("Using natural planet for " + starLord);
                }
            }

//  	    	if(jOrSPlanet == null) {
//  	    		jOrSPlanet = getStrongPlanet(currentPlanet.getHouse().getPlanets());
//  	    	}
        }

        return jOrSPlanet;

    }

    private Planet getStrongPlanet(List<PlanetInfo> planets, StringBuilder sb) {
        Planet strongPlanet = null;
        List<PlanetInfo> ownStarPlanets = new ArrayList<PlanetInfo>();

        boolean mercuryDeb = false;
        for (PlanetInfo planetInfo : planets) {

            if (planetInfo.getPlanetStar().getStarLord() == planetInfo.getPlanet()) {
                sb.append(planetInfo.getPlanet().toString()).append(",");
                //A planet is in debilitation and in its own star is only possible for Mercury.
                //If only mercury is in own star, he would anyway win in the above if Condition
                //Otherwise, we can safely exclude debilitation planet from competing planets.
                //Again this is only possible for mercury.

                if (planetInfo.getPlanet().getDelibitation() == planetInfo.getHouse().getRaasi()) {
                    mercuryDeb = true;
                    //If planet is debilitated, do not consider it for own star. This can only happen for mercury.
                    continue;
                }
                ownStarPlanets.add(planetInfo);
            }
        }

        if (ownStarPlanets.size() == 1) {
            if(mercuryDeb) {
                sb.append(" are in own star. However, Mercury is debilitated. So, Mercury can be ignored. ");
            }else{
                sb.append(" is in own star. ");
            }

            return ownStarPlanets.get(0).getPlanet();
        } else {
            if(ownStarPlanets.size() >0){
                sb.append(" are in own star. So, considering order of strengh to determine the strong planet. ");
            }else{
                sb.append("None of the digbala planets are in own star. So, considering order of strengh to determine the strong planet. ");
            }

            int strength = -1;
            //More than one db planet is in own star or none of the db planets in own star. Choose strongest planet based on order of strength
            for (PlanetInfo planetInfo : planets) {

                switch (planetInfo.getPlanet()) {

                    case RAHU:
                        strength = 1;
                        strongPlanet = planetInfo.getPlanet();
                        break;

                    case KETU:
                        if (strongPlanet == null) {
                            strength = 2;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 2) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 2;
                            }
                        }
                        break;

                    case SUN:
                        if (strongPlanet == null) {
                            strength = 3;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 3) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 3;
                            }
                        }
                        break;

                    case MOON:

                        if (strongPlanet == null) {
                            strength = 4;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 4) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 4;
                            }
                        }

                        break;
                    case VENUS:

                        if (strongPlanet == null) {
                            strength = 5;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 5) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 5;
                            }
                        }

                        break;
                    case JUPITER:

                        if (strongPlanet == null) {
                            strength = 6;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 6) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 6;
                            }
                        }

                        break;
                    case SATURN:

                        if (strongPlanet == null) {
                            strength = 7;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 7) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 7;
                            }
                        }

                        break;

                    case MARS:

                        if (strongPlanet == null) {
                            strength = 8;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 8) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 8;
                            }
                        }

                        break;


                    case MERCURY:

                        if (strongPlanet == null) {
                            strength = 9;
                            strongPlanet = planetInfo.getPlanet();
                        } else {
                            if (strength > 9) {
                                strongPlanet = planetInfo.getPlanet();
                                strength = 9;
                            }
                        }

                        break;

                    default:
                        strongPlanet = null;

                }
            }
        }

        if(strongPlanet != null) {
            sb.append("So, the strongest planet based on order of strength is ").append(strongPlanet.toString()).append(". ");
        }
        return strongPlanet;
    }


    private String getPredictions(BirthChart birthChart) {

        String[] houseNames = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th"};

        StringBuilder sb = new StringBuilder();
        sb.append("Observations:\n");

        int i = 0;

        Planet[] ykPlanets = birthChart.getRaasiChakra().getOrderedHouses().get(0).getRaasi().getYogaKarakas();

        for (House house : birthChart.getRaasiChakra().getOrderedHouses()) {

            if (i == 0) {
                sb.append("\n").append("Lagna pada is in ").append(getInitCap(birthChart.isSignsInEnglish() ? house.getRaasi().getEnglishName() : house.getRaasi().getName())).append(" and is posited in the star ");

                Planet houseLord = Planet.LAGNA;

                PlanetInfo currentPlanet = null;
                for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                    if (planetInfo.getPlanet() == houseLord) {
                        currentPlanet = planetInfo;
                        break;
                    }
                }

                sb.append(getInitCap(currentPlanet.getPlanetStar().name()) + " whose lord is " + getInitCap(currentPlanet.getPlanetStar().getStarLord().name()) + ".");


                if (currentPlanet.isHasDikbala()) {
                    sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " has digbala.");
                }

                if (currentPlanet.isInYkStar()) {
                    sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in YK star.");
                }
                if ("UD".equalsIgnoreCase(currentPlanet.getUdStatus())) {
                    sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in UD.");
                }
                if ("VUD".equalsIgnoreCase(currentPlanet.getUdStatus())) {
                    sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in VUD.");
                }
                sb.append("\n");
            }
            sb.append("\n").append(houseNames[i]).append(" house is ").append(getInitCap(birthChart.isSignsInEnglish() ? house.getRaasi().getEnglishName() : house.getRaasi().getName())).append(" and its lord ").append(getInitCap(house.getRaasi().getRaasiLord().toString())).append(" is posited in the star ");

            Planet houseLord = house.getRaasi().getRaasiLord();

            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == houseLord) {
                    currentPlanet = planetInfo;
                    break;
                }
            }

            sb.append(getInitCap(currentPlanet.getPlanetStar().name()) + " whose lord is " + getInitCap(currentPlanet.getPlanetStar().getStarLord().name()) + ".");


            if (currentPlanet.isHasDikbala()) {
                sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " has digbala.");
            }

            if (currentPlanet.isInYkStar()) {
                sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in YK star.");
            }
            if ("UD".equalsIgnoreCase(currentPlanet.getUdStatus())) {
                sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in UD.");
            }
            if ("VUD".equalsIgnoreCase(currentPlanet.getUdStatus())) {
                sb.append("HL " + getInitCap(currentPlanet.getPlanet().name()) + " is in VUD.");
            }


            if (i > 0) {

                Planet starLord = currentPlanet.getPlanetStar().getStarLord();

                PlanetInfo starLordPlanet = null;
                for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                    if (planetInfo.getPlanet() == starLord) {
                        starLordPlanet = planetInfo;
                        break;
                    }
                }

//				System.out.println("SL " + starLordPlanet.getPlanet().name());


                if (starLordPlanet.isHasDikbala()) {
                    sb.append(" SL " + getInitCap(starLordPlanet.getPlanet().name()) + " has digbala.");
                }
                if (starLordPlanet.isInYkStar()) {
                    sb.append(" SL " + getInitCap(starLordPlanet.getPlanet().name()) + " is in YK star.");
                }
                if ("UD".equalsIgnoreCase(starLordPlanet.getUdStatus())) {
                    sb.append(" SL " + getInitCap(starLordPlanet.getPlanet().name()) + " is in UD.");
                }
                if ("VUD".equalsIgnoreCase(starLordPlanet.getUdStatus())) {
                    sb.append(" SL " + getInitCap(starLordPlanet.getPlanet().name()) + " is in VUD.");
                }


                boolean found = false;
                for (PlanetInfo planetInfo : starLordPlanet.getHouse().getPlanets()) {
                    if (planetInfo.getPlanet().getExaltation() == planetInfo.getHouse().getRaasi()) {
                        sb.append(" ").append(planetInfo.getPlanet()).append(" is ").append(" Exalted.");
                    }

                    for (Planet ykPlanet : ykPlanets) {
                        if (ykPlanet == planetInfo.getPlanet()) {
                            if (starLordPlanet.getPlanet() == ykPlanet) {
                                sb.append(" SL is YK.");
                            } else {
                                sb.append(" SL is with YK planet.");
                            }
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

            }
            sb.append("\n");

            i++;
        }

        sb.append("\n");

        return sb.toString();

    }


	private PdfPTable addPlanetTable(BirthChart birthChart) throws DocumentException{
    	
    	
   	 PdfPTable table = new PdfPTable(9);
   	 
   	 String[ ] headers = {"Planet", "Sign", "Degrees",  "Star", "Star Lord","Kal Lord","KL Guna", "KL Jeeva", "KL Sareera" };
     
   	 table.setWidths(new int[]{ 12,25,18,30, 15,15,15,15,15 });
   	 
   	 float height = 18.0F;
   	 
   	 for(String header: headers) {
   		 PdfPCell cell = new PdfPCell();
   		 cell.setFixedHeight(height); 
   		 cell.setNoWrap(false);
// 	    	 cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            cell.addElement(new Phrase(header, new Font(this.baseFont, HoraStrings.EXTRA_SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(cell);
        }


        for (Planet planet : Planet.values()) {

            PdfPCell planetNameCell = new PdfPCell();
            planetNameCell.setFixedHeight(height);
            planetNameCell.addElement(new Phrase(getInitCap(planet.getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(planetNameCell);

            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == planet) {
                    currentPlanet = planetInfo;
                    break;
                }
            }

            PdfPCell planetSignCell = new PdfPCell();
            planetSignCell.setFixedHeight(height);


            if (birthChart.isSignsInEnglish()) {
                planetSignCell.addElement(new Phrase(getInitCap(currentPlanet.getHouse().getRaasi().getEnglishShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            } else {
                planetSignCell.addElement(new Phrase(getInitCap(currentPlanet.getHouse().getRaasi().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            }

            table.addCell(planetSignCell);

//	    System.out.println("Star/longi " + (currentPlanet.getHouse().getRaasi().getRaasiNo() * 30 + currentPlanet.longitude)  + "/" +  currentPlanet.longitude);
	    
	    double longitude = (currentPlanet.getHouse().getRaasi().getRaasiNo() * 30 + currentPlanet.longitude) ;
	    
//	    System.out.println("Planet longitude: " + currentPlanet.longitude + "  " + getDM(currentPlanet.longitude));


            PdfPCell degreeCell = new PdfPCell();
            degreeCell.setFixedHeight(height);
            degreeCell.addElement(new Phrase(getDMS(currentPlanet.longitude), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(degreeCell);


            int nakshatra = ((int) Math.floor(longitude / 13.333333333333334D));
            double balanceOfNakshatra = (1.0D - (longitude / 13.333333333333334D - nakshatra));
            int pada = ((int) Math.floor((1.0D - balanceOfNakshatra) / 0.25D) + 1);
            String starName = HoraStrings.NAKSHATRAS[nakshatra];

            Star star = Star.valueOf(starName);

// 	    currentPlanet.setPlanetStar(star);
 	    
		PdfPCell planetStarCell = new PdfPCell();
		planetStarCell.setFixedHeight(height);
		planetStarCell.addElement(new Phrase(getInitCap(star.name()) + "(" + String.valueOf(pada) + ")", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
	    table.addCell(planetStarCell);
     		
		PdfPCell planetStarLordCell = new PdfPCell();
		planetStarLordCell.setFixedHeight(height);
		planetStarLordCell.addElement(new Phrase(getInitCap(star.getStarLord().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
	    table.addCell(planetStarLordCell);
	     
	    
	    Planet kalamsaLord = getKalamsaLord(currentPlanet.getHouse().getRaasi().getRaasiNo(), currentPlanet.longitude);
	    
//	    System.out.println("Planet " + currentPlanet.getPlanet() +  " KalamsaLord: " + kalamsaLord + " longitude: " + currentPlanet.longitude + " star: " + star.name() );
  	    
	    Planet[] jsPlanets = getJeevaSareeraPlanets(birthChart, kalamsaLord, currentPlanet);
  	     
//	    System.out.println("JS Planets: " + Arrays.toString( jsPlanets) );

//	    String exaltDelib = "";
//	    if( currentPlanet.getHouse().getRaasi() == currentPlanet.getPlanet().getExaltation() ) { 
//	    	exaltDelib = "Exalted";
//	    }else if( currentPlanet.getHouse().getRaasi() == currentPlanet.getPlanet().getDelibitation()){
//	    	exaltDelib = "Debilited";
//	    }

            PdfPCell kalamsaLordCell = new PdfPCell();
            kalamsaLordCell.setFixedHeight(height);
            kalamsaLordCell.addElement(new Phrase(getInitCap(kalamsaLord.getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(kalamsaLordCell);


//	    String kshetram = "";
//	    
//	    if(currentPlanet.getHouse().getRaasi().getRaasiLord() == currentPlanet.getPlanet()) {
//	    	kshetram  = "Own";
//	    }

            String ud = "";
            if (star.getUd1() == pada || star.getUd2() == pada) {
                ud = "UD";
            } else if (star.getvUd1() == pada || star.getvUd2() == pada) {
                ud = "VUD";
            }

            if (planet != Planet.LAGNA) {
                currentPlanet.setUdStatus(ud);
            }

            Planet[] ykPlanets = birthChart.getRaasiChakra().getLagna().getRaasi().getYogaKarakas();

            for (Planet ykPlanet : ykPlanets) {
                if (ykPlanet == star.getStarLord()) {
                    currentPlanet.setInYkStar(true);
                    break;
                }
            }

            String threeFoldGuna = getThreeFoldGuna(birthChart, kalamsaLord);

            PdfPCell threeFoldGunaCell = new PdfPCell();
            threeFoldGunaCell.setFixedHeight(height);
            threeFoldGunaCell.addElement(new Phrase(threeFoldGuna, new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(threeFoldGunaCell);


            PdfPCell jeevaPlanetCell = new PdfPCell();
            jeevaPlanetCell.setFixedHeight(height);
            jeevaPlanetCell.addElement(new Phrase(getInitCap(jsPlanets[0].getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(jeevaPlanetCell);


//	    System.out.println("GunaStarNo : " + gunaStarNo);
//	    System.out.println("threeFoldGunas : " + threeFoldGunas);

            PdfPCell sareeraPlanetCell = new PdfPCell();
            sareeraPlanetCell.setFixedHeight(height);
            sareeraPlanetCell.addElement(new Phrase(getInitCap(jsPlanets[1].getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(sareeraPlanetCell);
        }


        return table;
//   	 this.document.add(table);
  
   	
   }
	
	public String getInitCap(String value){
		 
		if(value!=null && value.length() > 0) {
			 
			value = value.trim().toLowerCase();
			value = value.substring(0,1).toUpperCase()+ value.substring(1);
		}
		return value;  		
		
	}
	 
	private String getThreeFoldGuna(BirthChart birthChart, Planet planet) {
		
		PlanetInfo currentPlanet = null; 
		for(PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
			if(planetInfo.getPlanet() == planet) {
				currentPlanet = planetInfo;
		    	break;
	 	    }
	 	}
		
		double longitude = (currentPlanet.getHouse().getRaasi().getRaasiNo() * 30 + currentPlanet.longitude) ;
		
	    int nakshatra = ((int)Math.floor(longitude / 13.333333333333334D));
	    double balanceOfNakshatra = (1.0D - (longitude  / 13.333333333333334D - nakshatra));
	    int pada = ((int)Math.floor((1.0D - balanceOfNakshatra) / 0.25D) + 1);
// 	    String starName = HoraStrings.NAKSHATRAS[nakshatra];
 	    
// 	    Star star = Star.valueOf(starName);
 	     
//	    String guna=""+ star.getStarLord().getGuna();
        int gunaStarNo = nakshatra % 9; //They repeat for a set of 9 stars
        String[][] threeFoldGunas = planet.getThreeFoldGunas();
        String threeFoldGuna = threeFoldGunas[pada - 1][gunaStarNo];

        if (planet == Planet.LAGNA) {
            threeFoldGuna = "";
        }
        return threeFoldGuna;
    }

    private String getDM(double value) {
        String retVal = "";
        int deg = 0;
        int min = 0, sec = 0;

 		deg = (int)Math.floor(value);
  		min = (int)Math.floor((value  - deg) * 60);
		sec = (int)Math.floor(  ( (value  - deg  -  min/60) * 3600) - (min * 60) ) ;
   				
		retVal = deg + "° " + min + "'" + sec + "''";

        return retVal;
    }

    private String getDMS(double value) {
        String retVal = "";
        int deg = 0;
        int min = 0, sec = 0;

        deg = (int) Math.floor(value);
        min = (int) Math.floor((value - deg) * 60);
        sec = (int) Math.floor(((value - deg - min / 60) * 3600) - (min * 60));

        retVal = deg + ":" + min + ":" + sec;

        return retVal;
    }

	private PdfPTable addMiscTable(BirthChart birthChart) throws DocumentException{
    	
    	
	   	 PdfPTable table = new PdfPTable(6);
	   	   
	   	 table.setWidths(new int[]{ 15,25,25,25,25,25 });
	   	 
	   	 
	   	 String[ ] headers = {"YK Planets", "Planets In YK Star", "Digbala Planets", "UD Planets", "VUD Planets", "Bhinna Pada Planets"};
  
	   	 table.setWidths(new int[]{ 15,25,25,25,25,25 });

        float height = 13.0F;

        for (String header : headers) {
            PdfPCell cell = getCell(header, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        height = 20.0F;

        Planet[] ykPlanets = birthChart.getRaasiChakra().getLagna().getRaasi().getYogaKarakas();

        StringBuffer sb = new StringBuffer();

        for (Planet planet : ykPlanets) {
            sb.append(getInitCap(planet.getShortName())).append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }


        PdfPCell yogaKarakaCell = new PdfPCell();
        yogaKarakaCell.setFixedHeight(height);
        yogaKarakaCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        yogaKarakaCell.setNoWrap(false);
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

        sb = new StringBuffer();
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if (planetInfo.isInYkStar()) {
                sb.append(getInitCap(planetInfo.getPlanet().getShortName())).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }


        PdfPCell ykStarPlanetsCell = new PdfPCell();
        ykStarPlanetsCell.setFixedHeight(height);
        ykStarPlanetsCell.setNoWrap(false);
        ykStarPlanetsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//		    table.addCell( ykStarPlanetsCell );


        PdfPCell chartRulingCell = new PdfPCell();
        chartRulingCell.setFixedHeight(height);
        chartRulingCell.setNoWrap(false);
//		    chartRulingCell.addElement(new Phrase(getWrapped( getDigbalaPlanets(birthChart)), new Font(this.baseFont, astrologytraining.com.horoscopegenerator.HoraStrings.MEDIUM_FONT_SIZE , 0, BaseColor.BLACK)));
        table.addCell(getCell(getWrapped(getDigbalaPlanets(birthChart)), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//		    table.addCell( chartRulingCell );

        sb = new StringBuffer();
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if ("UD".equalsIgnoreCase(planetInfo.getUdStatus()) && planetInfo.getPlanet() != Planet.LAGNA) {
                sb.append(getInitCap(planetInfo.getPlanet().getShortName())).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }


        PdfPCell udPlanetsCell = new PdfPCell();
        udPlanetsCell.setFixedHeight(height);
        udPlanetsCell.setNoWrap(false);
        udPlanetsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//		    table.addCell( udPlanetsCell );


        sb = new StringBuffer();
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if ("VUD".equalsIgnoreCase(planetInfo.getUdStatus()) && planetInfo.getPlanet() != Planet.LAGNA) {
                sb.append(getInitCap(planetInfo.getPlanet().getShortName())).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }


        PdfPCell vudPlanetsCell = new PdfPCell();
        vudPlanetsCell.setFixedHeight(height);
        vudPlanetsCell.setNoWrap(false);
        vudPlanetsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//		    table.addCell( vudPlanetsCell );

        sb = new StringBuffer();
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            int houseNo = planetInfo.getHouse().getAssignedHouseNo();
            //if(houseNo == 6 || houseNo == 8 || houseNo == 12 ){
            if (isInBhinnaPada(planetInfo.getPlanetStar())) {
                sb.append(getInitCap(planetInfo.getPlanet().getShortName())).append(",");
            }
            //}
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        PdfPCell bhinnaPaadaPlanetsCell = new PdfPCell();
        bhinnaPaadaPlanetsCell.setFixedHeight(height);
        bhinnaPaadaPlanetsCell.setNoWrap(false);
        bhinnaPaadaPlanetsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//		    table.addCell( bhinnaPaadaPlanetsCell );
 		    
		    return table;
	   }
	
	private Set<Planet> getRepresentations(Planet planet, BirthChart birthChart ){
		
		Set<Planet> planets = new LinkedHashSet<Planet>();
		
		PlanetInfo currentPlanet = null; 
		for(PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
			if(planetInfo.getPlanet() == planet) {
				currentPlanet = planetInfo;
		    	break;
			}
	 	}
		
		//Rahu/Ketu posited house lord
		planets.add( currentPlanet.getHouse().getRaasi().getRaasiLord());
		
		//Rahu posited star lord
		planets.add( currentPlanet.getPlanetStar().getStarLord());
		
		//Rahu conjunction
		for(PlanetInfo planetInfo: currentPlanet.getHouse().getPlanets()) {
			planets.add( planetInfo.getPlanet());
		}
		
		///Rahu aspected by 
		for(PlanetInfo planetInfo: currentPlanet.getHouse().getAspectedPlanets()) {
			planets.add( planetInfo.getPlanet());
		}
		     
		return planets;
 	}
	
	
	
	private PdfPTable addHelp() throws DocumentException, ParseException{
		PdfPTable table = new PdfPTable(1);
 	   	table.setWidths(new int[]{ 300});
 	   	 
	   	String[ ] headers = {"HNO - House Number, HL- House Lord/Planet, Deg - Degrees, HL Star - HL Posited Star, HL Gu - HL Gunas," + 
	   			 			  "HL Jee - HL Jeeva Planet, HL Sar - HL Sareera Planet, SL - House Lord/Planet Posited Star Lord,",
	   			 			  "SL Gu - Star Lord Gunas, SL Jee - Star Lord Jeeva Planet, SL Sar - Star Lord Sareera Planet, KL - Kalamsa Lord,"  +
	   			 			  "KL Gu - KL Gunas, KL Jee - KL Jeeva Planet, KL Sar - KL Sareera Planet," ,
	   			 			  "YK - Yoga Karaka, UD - Uttama Drekkana, VUD - Vargotthama Uttama Drekkana"};
	   	 		 
		for(String header: headers) {
 	   		PdfPCell cell =  getCell(header, 12, HoraStrings.SMALL_FONT_SIZE );
	   		table.addCell(cell);
	   		cell.setBorder(Rectangle.NO_BORDER);
	   	}
		table.getRow(0).getCells()[0].setBorder(Rectangle.NO_BORDER);
		table.getRow(1).getCells()[0].setBorder(Rectangle.NO_BORDER);
		table.getRow(2).getCells()[0].setBorder(Rectangle.NO_BORDER);
		return table;
	}
	 
	private PdfPTable addDBA(BirthChart birthChart) throws DocumentException, ParseException{
		PdfPTable table = new PdfPTable(3);
	   	table.setWidths(new int[]{ 25,25, 25});
	   	String[ ] headers = {"Dasa", "Bhukthi", "Antara"};
	   	table.setWidths(new int[]{ 25,25, 25 });
 	   	  
		for(String header: headers) {
  	   		PdfPCell cell =  getCell(header, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE );
	   		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	   		table.addCell(cell);
 	   	}
 
	   	String dasa = "";
	    String[] baArray = null;
	 		    
		boolean dasaFound = false;
		    
		for (int i = 0; i < this.dasaDetails.length; i++) {
		     
			String dateStr = dasaDetails[i][0].substring( dasaDetails[i][0].indexOf(":")+2);
			String fromDateStr = dateStr.substring(0, dateStr.indexOf("to")-1);
			String toDateStr = dateStr.substring( dateStr.indexOf("to")+3);
			 
			Date fromDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(fromDateStr);
			Date toDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(toDateStr);
			 
			Date curDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(curDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
  		     
 		    if(dasaFound){
				cal.add(Calendar.YEAR, 5);
				curDate = cal.getTime();
				break;
  		    }
  		      
 		    if((fromDate.before(curDate) || fromDate.equals(curDate) ) && (toDate.after(curDate) || toDate.equals(curDate ))) {
 		    	dasaFound = true; 
 		    	 
			    dasa = dasaDetails[i][0];
                boolean hasAntaraSplit = false;
                for (int j = 0; j < this.dasaDetails[i].length - 1; j++) {
                    String possibleEndDate = null;

                    if(j+2 < this.dasaDetails[i].length) {
                        possibleEndDate = getEndDate(dasaDetails[i][j + 2]);
                    }
			    	baArray = getBA(dasaDetails[i][j+1], possibleEndDate, curDate);

//                    if(j< this.dasaDetails[i].length){ //When Antaradasa is split between 2 bhukthis'
//                        hasAntaraSplit = hasAntaraSplit(dasaDetails[i][j+2], curDate);
//                    }
//                    if(hasAntaraSplit){
//
//                        String endDate = getEndDate(dasaDetails[i][j+2]);
//                        String startDate = getStartDate(dasaDetails[i][j]);
//                        int x=1;
//
//                    }
//			    	else
                    //baArray[2] == null means further adjustment is needed
                    if(baArray != null && baArray[2] == null) {
			    		  String endDate = "";
			    		  if(j+2 < this.dasaDetails[i].length) {
				    		  endDate = getEndDate(dasaDetails[i][j+2]);
			    		  }else{
			    			  dateStr = dasaDetails[i+1][0].substring( dasaDetails[i+1][0].indexOf(":")+2);
			    			  endDate = dateStr.substring(0, dateStr.indexOf("to")-1);
			    		  }
			    		  baArray[0] = baArray[0] + endDate;
			    		  
			    		  if(baArray[1].endsWith("to ")) {
			    			  baArray[1] = baArray[1] + endDate;
			    		  }
  			    		  break;
			    	  }else if(baArray!=null){
                        break;
                    }
			    }
  			    //This is very rare scenario where the last bhukthi overlaps the current date
 			    if(baArray == null) {
 			    	baArray = getLastBA(dasaDetails[i][this.dasaDetails[i].length-1], toDateStr);
 			    }
  			    break;
		    }else {
		    	if(dasaFound){
		    		break;
		    	}
		    }
		}
			table.addCell( getCell(getWrapped( dasa), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ) );
 			table.addCell( getCell(getWrapped( baArray !=null && baArray.length > 0 ? baArray[0] : ""), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ) );
 			table.addCell( getCell(getWrapped( baArray !=null && baArray.length > 0 ? baArray[1] : ""), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE ) );
 	        return table;
	   }
	
	private String getEndDate(String baDetails) {
 		String[] bhukthiArray = baDetails.split("Bhukthi\n");
		String[] antaraArray = bhukthiArray[1].split("\n");
		String[] currentAntara = antaraArray[0].split(" ");
		return currentAntara[1] ;
 	}
    private String getStartDateWithPlanet(String baDetails) {
        String[] bhukthiArray = baDetails.split("Bhukthi\n");
        String[] antaraArray = bhukthiArray[1].split("\n");
//        String[] currentAntara = antaraArray[antaraArray.length-1].split(" ");
        return antaraArray[antaraArray.length-1];
    }

    private boolean hasAntaraSplit(String baDetails, Date curDate) throws ParseException {

        String[] bhukthiArray = baDetails.split("Bhukthi\n");
        String[] antaraArray = bhukthiArray[1].split("\n");
        String[] currentAntara = antaraArray[0].split(" ");
        Date fromDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(currentAntara[1]);

        if(fromDate.after(curDate) || fromDate.equals(curDate)) {
            return true;
        }

        return false;
    }
	
	private String[] getBA(String baDetails, String possibleEndDate, Date curDate) throws ParseException {
		
//		System.out.println("Trying to find " + curDate + " in " + baDetails);

        String[] bhukthiArray = baDetails.split("Bhukthi\n");

        String[] baResult = new String[3];

        baResult[0] = bhukthiArray[0] + " Bhukthi : ";

        String[] antaraArray = bhukthiArray[1].split("\n");

//		System.out.println("Bhukthi " + baResult[0]);
//		System.out.println("Antara Array");
//		System.out.println(Arrays.toString(antaraArray));


        String antara = null;
        for (int i = 0; i < antaraArray.length; i++) {

            String[] currentAntara = antaraArray[i].split(" ");
            Date fromDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(currentAntara[1]);

            if (fromDate.after(curDate)) {
                if (i - 1 > 0) {
                    String[] prevAntara = antaraArray[i - 1].split(" ");
                    antara = prevAntara[0] + " Antara :" + prevAntara[1] + " to " + currentAntara[1];
                    break;
                } else {
                    System.out.println("Caught");
                }
            } else if (fromDate.equals(curDate)) {
                String toDate = "";
                if (i != antaraArray.length - 1) {
                    String[] nextAntara = antaraArray[i].split(" ");
                    toDate = nextAntara[1];
                }
                antara = currentAntara[0] + " Antara :" + currentAntara[1] + " to " + toDate;
                break;
            }
        }

        if (antara != null) {
            baResult[1] = antara;
        }

//		System.out.println("BA Result " + Arrays.toString(baResult));

        if (baResult[1] != null) {

            String[] startBhukthi = antaraArray[0].split(" ");
            String[] endBhukthi = antaraArray[antaraArray.length - 1].split(" ");

            baResult[0] = baResult[0] + startBhukthi[1] + " to ";

            return baResult;
        } else {
            if(possibleEndDate!=null && !possibleEndDate.trim().isEmpty()) {
                Date fromDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(possibleEndDate);
                if (fromDate.after(curDate) || fromDate.equals(curDate)) {
                    //Means there is a split. Let us form the string now.
                    baResult[0] = bhukthiArray[0] + " Bhukthi : " + getEndDate(baDetails) + " to " + possibleEndDate;
                    String antaraStr = getStartDateWithPlanet(baDetails);
                    String[] antarArray = antaraStr.split(" ");
                    baResult[1] = antarArray[0] + " Antara :" + antarArray[1] +  " to " + possibleEndDate;
                    baResult[2] ="ACCURATE";
                    return baResult;
                }
            }
        }

        return null;
    }

    private String[] getLastBA(String baDetails, String dasaEndDate) throws ParseException {


        String[] bhukthiArray = baDetails.split("Bhukthi\n");

        String[] baResult = new String[2];

        baResult[0] = bhukthiArray[0] + " Bhukthi : ";

        String[] antaraArray = bhukthiArray[1].split("\n");

        String[] firstAntara = antaraArray[0].split(" ");
        String[] lastAntara = antaraArray[antaraArray.length - 1].split(" ");

        baResult[0] = baResult[0] + firstAntara[1] + " to " + dasaEndDate;
        baResult[1] = lastAntara[0] + " Antara : " + lastAntara[1] + " to " + dasaEndDate;

        return baResult;
    }

    private PdfPTable addRahuKetuRepTable(BirthChart birthChart) throws DocumentException {


        PdfPTable table = new PdfPTable(2);

        table.setWidths(new int[]{25, 25});


        String[] headers = {"Rahu Representations", "Ketu Representations"};

        table.setWidths(new int[]{25, 25});

        float height = 13.0F;

        for (String header : headers) {
            PdfPCell cell = getCell(header, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }


        height = 20.0F;

        Set<Planet> rahuReps = getRepresentations(Planet.RAHU, birthChart);

        StringBuffer sb = new StringBuffer();

        for (Planet planet : rahuReps) {
            if (planet == Planet.RAHU || planet == Planet.KETU || planet == Planet.LAGNA)
                continue;
            sb.append(getInitCap(planet.getShortName())).append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        PdfPCell rahuRepsCell = new PdfPCell();
        rahuRepsCell.setFixedHeight(height);
        rahuRepsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));

        rahuRepsCell.setNoWrap(false);
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//			table.addCell( rahuRepsCell );


        Set<Planet> ketuReps = getRepresentations(Planet.KETU, birthChart);

        sb = new StringBuffer();

        for (Planet planet : ketuReps) {
            if (planet == Planet.RAHU || planet == Planet.KETU || planet == Planet.LAGNA)
                continue;
            sb.append(getInitCap(planet.getShortName())).append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        PdfPCell ketuRepsCell = new PdfPCell();
        ketuRepsCell.setFixedHeight(height);
        ketuRepsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.MEDIUM_FONT_SIZE, 0, BaseColor.BLACK)));
        ketuRepsCell.setNoWrap(false);
        table.addCell(getCell(getWrapped(sb.toString()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
//			table.addCell( ketuRepsCell );

        return table;
    }

    private String getWrapped(String input) {

        if (input != null) {
            return input.replace(",", ", ");
        }
        return input;

    }

    private boolean isInBhinnaPada(Star planetStar) {


        for (Star star : BhinnaPadaStars.bhinnaPaadaStars) {
            if (planetStar == star) {
                return true;
            }
        }

        return false;

    }

	private PdfPTable addMiscTable2(BirthChart birthChart) throws DocumentException{
     
	   	 PdfPTable table = new PdfPTable(6);
	   	 
	   	 String[ ] headers = {"Planet", "Star", "SL", "Guna", "Jeeva", "Sareera" };
	     
	   	 table.setWidths(new int[]{ 14,40, 15, 18,15,15} );
	   	 
	   	 float height = 12.0F;
	   	 
	   	 for(String header: headers) {
	   		 PdfPCell cell = new PdfPCell();
	   		 cell.setFixedHeight(height); 
//	 	    	 cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            cell.addElement(new Phrase(header, new Font(this.baseFont, HoraStrings.EXTRA_SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(cell);
        }


        for (Planet planet : Planet.values()) {

            if (planet == Planet.LAGNA)
                continue;


            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == planet) {
                    currentPlanet = planetInfo;
                    break;
                }
            }


            PdfPCell planetNameCell = new PdfPCell();
            planetNameCell.setFixedHeight(height);
            planetNameCell.addElement(new Phrase(getInitCap(planet.getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(planetNameCell);


            Planet[] jsPlanets = getJeevaSareeraPlanets(birthChart, planet, currentPlanet);

            String threeFoldGuna = getThreeFoldGuna(birthChart, planet);

            PdfPCell starCell = new PdfPCell();
            starCell.setFixedHeight(height);
            starCell.addElement(new Phrase(getInitCap(currentPlanet.getPlanetStar().name()) + "(" + String.valueOf(currentPlanet.getPada()) + ")", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(starCell);

            PdfPCell starLordCell = new PdfPCell();
            starLordCell.setFixedHeight(height);
            starLordCell.addElement(new Phrase(getInitCap(currentPlanet.getPlanetStar().getStarLord().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(starLordCell);


            PdfPCell threeFoldGunaCell = new PdfPCell();
            threeFoldGunaCell.setFixedHeight(height);
            threeFoldGunaCell.addElement(new Phrase(threeFoldGuna, new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(threeFoldGunaCell);


            PdfPCell jeevaPlanetCell = new PdfPCell();
            jeevaPlanetCell.setFixedHeight(height);
            jeevaPlanetCell.addElement(new Phrase(getInitCap(jsPlanets[0].getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(jeevaPlanetCell);

            PdfPCell sareeraPlanetCell = new PdfPCell();
            sareeraPlanetCell.setFixedHeight(height);
            sareeraPlanetCell.addElement(new Phrase(getInitCap(jsPlanets[1].getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(sareeraPlanetCell);
        }
        return table;

    }


    private void calcJSAndGuna(BirthChart birthChart) throws DocumentException {
        for (Planet planet : Planet.values()) {
            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == planet) {
                    currentPlanet = planetInfo;
                    break;
                }
            }
            Planet[] jsPlanets = getJeevaSareeraPlanets(birthChart, planet, currentPlanet);
            String threeFoldGuna = getThreeFoldGuna(birthChart, planet);
            currentPlanet.setGunas(threeFoldGuna);
            currentPlanet.setJeeva(jsPlanets[0]);
            currentPlanet.setSareera(jsPlanets[1]);

            Planet kalamsaLord = getKalamsaLord(currentPlanet.getHouse().getRaasi().getRaasiNo(), currentPlanet.longitude);
            currentPlanet.setKalamsaLord(kalamsaLord);
        }
    }


    private String getDigbalaPlanets(BirthChart birthChart) {

        StringBuffer sb = new StringBuffer();

        for (Planet planet : Planet.values()) {

            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == planet) {
                    currentPlanet = planetInfo;
                    break;
                }
            }

            if (currentPlanet.isHasDikbala()) {
                sb.append(getInitCap(currentPlanet.getPlanet().getShortName())).append(",");
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

		return sb.toString();
	}
	
	private PdfPTable addHousesTable(BirthChart birthChart) throws DocumentException{
    	
    	
	   	 PdfPTable table = new PdfPTable(16);
	   	 
	   	 String[ ] headers = {"HNo", "Sign", "HL" , "Deg", "HL Star", "HL Gu", "HL Jee", "HL Sar" , 
	   			 			  "SL", "SL Gu", "SL Jee", "SL Sar",
	   			 			  "KL", "KL Gu", "KL Jee", "KL Sar"};
 	   	 
	   	 table.setWidths(new int[]{ 6,12,12,13, 20,8,8,8,8,8,8,8,8,8,8,8 });
  	   	 
	   	 for(String header: headers) {
 	   		 PdfPCell cell =  getCell(header, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE );
	   		 cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	   		 table.addCell(cell);
	   	 }
 
	   	
	   	 int i=0;
	   	 Set<Integer> finishedHouses = new HashSet<Integer>(12);
  	   	for(House house: birthChart.getRaasiChakra().getOrderedHouses()){
  	   		
  	   		int houseNo = house.getAssignedHouseNo();
  	   		String otherHouseName = null;
  	   		
  	   		if (!finishedHouses.contains(houseNo)) {
  	   		
	  	   		int otherHouse = getOtherHouse(birthChart.getRaasiChakra().getOrderedHouses(),  house.getRaasi()  );
	  	   		
	  	   		if(otherHouse != -1) {
	  	   			for(House tempHouse: birthChart.getRaasiChakra().getOrderedHouses()){
	  	   				
	  	   				if(tempHouse.getAssignedHouseNo() == otherHouse) {
	  	   					
		  	   				if(birthChart.isSignsInEnglish()) {
		  	   					otherHouseName =  tempHouse!=null? getInitCap(tempHouse.getRaasi().getEnglishShortName()) : "-" ;
		  	   				}else{
		  	   					otherHouseName = tempHouse!=null? getInitCap(tempHouse.getRaasi().getShortName()) : "-" ;
		  	   				}
	  	    
	  	   				}
	  	   			}
	  	   			
	  	   		}
 	  	   		
	  	   		String houseNumStr = houseNo + (otherHouse == -1 ? "" : "," + otherHouse);
	    	   		
	  	   		if(i==0){
	  	   			addRowForHouseOrPlanet(birthChart, table,  house, Planet.LAGNA, "1", null);
	  	   			i++;
	  	   		}
	  	   		addRowForHouseOrPlanet(birthChart, table, house, null, houseNumStr, otherHouseName);
	  	   		finishedHouses.add(houseNo);
	  	   		finishedHouses.add(otherHouse);
  	   		}
  	   		
  	   	}
  	   	addRowForHouseOrPlanet(birthChart, table, null, Planet.RAHU, null, null);
  	   	addRowForHouseOrPlanet(birthChart, table, null, Planet.KETU, null, null);
  	   	
  		return table;
  	  
	}
	
	private int getOtherHouse(List<House> houses, Raasi currentRaasi){
		
		Raasi searchFor = null;
		
		switch(currentRaasi) {
		case MESHAM:
			searchFor = Raasi.VRUSCHIKA;
			break;
		case VRISHABHAM:
			searchFor = Raasi.THULA;
			break;
		case MIDHUNAM:
			searchFor = Raasi.KANYA;
			break;
		case KARKATAKAM:
			searchFor = null;
			break;
		case SIMHAM:
			searchFor = null;
			break;
		case KANYA:
			searchFor = Raasi.MIDHUNAM;
			break;
		case THULA:
			searchFor = Raasi.VRISHABHAM;
			break;
		case VRUSCHIKA:
			searchFor = Raasi.MESHAM;
			break;
		case DHANUS:
			searchFor  = Raasi.MEENAM;
			break;
		case MAKARAM:
			searchFor = Raasi.KUMBHAM;
			break;
		case KUMBHAM:
			searchFor = Raasi.MAKARAM;
			break;
		case MEENAM:
			searchFor = Raasi.DHANUS;
			break;
		}
		if(searchFor == null)
			return -1;
		
		for(House house: houses){
			if (house.getRaasi() == searchFor){
				return house.getAssignedHouseNo();
			}
		}
		
		return -1;
		
	}
	
	private PdfPCell getCell(String content, float height, float contentFontSize) {
	 
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(height + 2);
		cell.setPadding(0f);
		cell.setPaddingLeft(1f);
		cell.setPaddingTop(0f);
		cell.setPaddingBottom(1f);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(new Phrase(content, new Font(this.baseFont,contentFontSize + 2, 0, BaseColor.BLACK)));
	 	return cell;
	}
  	  
  	  private void addRowForHouseOrPlanet(BirthChart birthChart,  PdfPTable table, House house, Planet planet, String houseNumStr, String otherHouseName){
  		  
	   		PdfPCell houseNoCell = new PdfPCell();
	   		houseNoCell.setFixedHeight(15.0F);
	   		
	   		float fontSize = HoraStrings.MEDIUM_FONT_SIZE;
  	   		houseNoCell.addElement(new Phrase(house!=null? String.valueOf(house.getAssignedHouseNo()):"-" , new Font(this.baseFont, fontSize, 0, BaseColor.BLACK)));
      		table.addCell( getCell(houseNumStr!=null? houseNumStr :"-", 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
	 	   		 
	   		String raasiName = "";
	   		if(birthChart.isSignsInEnglish()) {
	   			raasiName =  house!=null? getInitCap(house.getRaasi().getEnglishShortName()) : "-" ;
			}else{
				raasiName = house!=null? getInitCap(house.getRaasi().getShortName()) : "-" ;
			}
	   		
	   		if(otherHouseName != null){
	   			raasiName = raasiName + "," + otherHouseName;
	   		}
	   		 
		    table.addCell( getCell(raasiName, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
		    
		    String planetName =getInitCap ( planet ==null? house.getRaasi().getRaasiLord().getShortName() : planet.getShortName());
		    if(planet == Planet.LAGNA) {
		    	planetName = "Lag Pada";
		    }
	  	    
 		   	table.addCell( getCell(planetName, 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
		    
		    Planet houseLord = null;
		    
		    if(planet != null)
 		    	houseLord = planet;
		    else
		    	houseLord = house.getRaasi().getRaasiLord();
 		     
		    PlanetInfo currentPlanet = null; 
		    for(PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
	 	    	if(planetInfo.getPlanet() == houseLord) {
		    		currentPlanet = planetInfo;
		    		break;
	 	    	}
 		    }

        table.addCell(getCell(getDMS(currentPlanet.longitude), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

        Star currentStar = currentPlanet.getPlanetStar();
        table.addCell(getCell(getInitCap(currentStar.name()) + "-" + String.valueOf(currentPlanet.getPada()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(planet == Planet.LAGNA ? "-" : currentPlanet.getGunas(), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));


        table.addCell(getCell(getInitCap(planet == Planet.LAGNA || currentPlanet.getJeeva() == null ? "-" : currentPlanet.getJeeva().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(getInitCap(planet == Planet.LAGNA || currentPlanet.getSareera() == null ? "-" : currentPlanet.getSareera().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

        PlanetInfo starLordPlanet = null;
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if (planetInfo.getPlanet() == currentStar.getStarLord()) {
                starLordPlanet = planetInfo;
                break;
            }
        }

        table.addCell(getCell(getInitCap(currentStar.getStarLord().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(starLordPlanet.getGunas(), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(getInitCap(starLordPlanet.getJeeva().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

        table.addCell(getCell(getInitCap(starLordPlanet.getSareera().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

        PlanetInfo kalamsaPlanet = null;
        for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
            if (planetInfo.getPlanet() == currentPlanet.getKalamsaLord()) {
                kalamsaPlanet = planetInfo;
                break;
            }
        }

        table.addCell(getCell(getInitCap(kalamsaPlanet.getPlanet().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(kalamsaPlanet.getGunas(), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(getInitCap(kalamsaPlanet.getJeeva().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));
        table.addCell(getCell(getInitCap(kalamsaPlanet.getSareera().getShortName()), 15, HoraStrings.EXTRA_MEDIUM_FONT_SIZE));

    }


	private PdfPTable addHouseTable(BirthChart birthChart) throws DocumentException{
    	
    	
	   	 PdfPTable table = new PdfPTable(9);
	   	 
	   	 String[ ] headers = {"HNo", "Sign", "H Lord", "House Lord Star", "SL", "Aspects", "HLGu", "HLJee", "HLSareera"};
	     
	   	 table.setWidths(new int[]{ 12,30,20,40,15,50,15,15,15 });
	   	 
	   	 for(String header: headers) {
	   		 PdfPCell cell = new PdfPCell();
	   		 cell.setFixedHeight(15.0F);
	   		 cell.setNoWrap(false);
	 	    	 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	    	 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	 cell.addElement(new Phrase(header, new Font(this.baseFont, HoraStrings.EXTRA_SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
		    	 table.addCell(cell);
	   	 }
  
	   	for(House house: birthChart.getRaasiChakra().getOrderedHouses()){


            PdfPCell houseNoCell = new PdfPCell();
            houseNoCell.setFixedHeight(15.0F);
            houseNoCell.addElement(new Phrase(String.valueOf(house.getAssignedHouseNo()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(houseNoCell);


            PdfPCell raasiCell = new PdfPCell();
            raasiCell.setFixedHeight(15.0F);
            if (birthChart.isSignsInEnglish()) {
                raasiCell.addElement(new Phrase(getInitCap(house.getRaasi().getEnglishShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            } else {
                raasiCell.addElement(new Phrase(getInitCap(house.getRaasi().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            }

            table.addCell(raasiCell);

            PdfPCell houseLordCell = new PdfPCell();
            houseLordCell.setFixedHeight(15.0F);
            houseLordCell.addElement(new Phrase(getInitCap(house.getRaasi().getRaasiLord().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(houseLordCell);

            Planet houseLord = house.getRaasi().getRaasiLord();

            PlanetInfo currentPlanet = null;
            for (PlanetInfo planetInfo : birthChart.getRaasiChakra().getPlanetInfo()) {
                if (planetInfo.getPlanet() == houseLord) {
                    currentPlanet = planetInfo;
                    break;
                }
            }

            double longitude = (currentPlanet.getHouse().getRaasi().getRaasiNo() * 30 + currentPlanet.longitude);

            int nakshatra = ((int) Math.floor(longitude / 13.333333333333334D));
            double balanceOfNakshatra = (1.0D - (longitude / 13.333333333333334D - nakshatra));
            int pada = ((int) Math.floor((1.0D - balanceOfNakshatra) / 0.25D) + 1);
            String starName = HoraStrings.NAKSHATRAS[nakshatra];

            Star star = Star.valueOf(starName);

            PdfPCell houseLordStarCell = new PdfPCell();
            houseLordStarCell.setFixedHeight(15.0F);
            houseLordStarCell.addElement(new Phrase(getInitCap(star.name()) + "(" + String.valueOf(pada) + ")", new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(houseLordStarCell);

            PdfPCell planetStarLordCell = new PdfPCell();
            planetStarLordCell.setFixedHeight(15.0F);
            planetStarLordCell.addElement(new Phrase(getInitCap(star.getStarLord().getShortName()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(planetStarLordCell);

            StringBuffer sb = new StringBuffer();
            for (PlanetInfo planetInfo : house.getAspectedPlanets()) {
                sb.append(getInitCap(planetInfo.getPlanet().getShortName())).append(",");
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            PdfPCell aspectsCell = new PdfPCell();
            aspectsCell.setFixedHeight(15.0F);
            aspectsCell.addElement(new Phrase(getWrapped(sb.toString()), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(aspectsCell);


            String threefoldGunas = getThreeFoldGuna(birthChart, house.getRaasi().getRaasiLord());
            PdfPCell threefoldGunaCell = new PdfPCell();
            threefoldGunaCell.setFixedHeight(15.0F);
            threefoldGunaCell.addElement(new Phrase(threefoldGunas, new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(threefoldGunaCell);

            Planet[] jsPlanets = getJeevaSareeraPlanets(birthChart, house.getRaasi().getRaasiLord(), currentPlanet);

            String jeevaPlanet = jsPlanets[0].getShortName();
            PdfPCell jeevaPlanetCell = new PdfPCell();
            jeevaPlanetCell.setFixedHeight(15.0F);
            jeevaPlanetCell.addElement(new Phrase(getInitCap(jeevaPlanet), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(jeevaPlanetCell);

            String sareeraPlanet = jsPlanets[1].getShortName();
            PdfPCell sareeraPlanetCell = new PdfPCell();
            sareeraPlanetCell.setFixedHeight(15.0F);
            sareeraPlanetCell.addElement(new Phrase(getInitCap(sareeraPlanet), new Font(this.baseFont, HoraStrings.SMALL_FONT_SIZE, 0, BaseColor.BLACK)));
            table.addCell(sareeraPlanetCell);


        }

        return table;
    }

    private Planet getKalamsaLord(int raasiNo, double longitude) {

        int arrayIndex = raasiNo % 4;
// 		 System.out.println("kalamsa: raasiNo: " + raasiNo + " raasiNo: " + (raasiNo%4) + " longitude: " + getDMS(longitude));
        int length = KalamsaLords.kalamsaRanges[arrayIndex].length;

        for (int i = 0; i < length; i++) {
            String[] kalamsaInfo = KalamsaLords.kalamsaRanges[arrayIndex][i].split(":");
            double value = Integer.parseInt(kalamsaInfo[0]) + Integer.parseInt(kalamsaInfo[1]) / 60d + Integer.parseInt(kalamsaInfo[2]) / 3600d;
            if (value > longitude) {
                return KalamsaLords.kalamsaLords[arrayIndex][i - 1];
            }
// 			  System.out.println("longitude " + longitude + "  range: " + value );
        }
        return KalamsaLords.kalamsaLords[arrayIndex][length - 1];
    }

    private PdfPTable addDasaTableToPDF() throws Exception {

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(95.0F);

        this.document.add(table);

        boolean dasaFound = false;

        int tableCount = 0;

        for (int i = 0; i < this.dasaDetails.length; i++) {

            String dateStr = dasaDetails[i][0].substring(dasaDetails[i][0].indexOf(":") + 2);
            String fromDateStr = dateStr.substring(0, dateStr.indexOf("to") - 1);
            String toDateStr = dateStr.substring(dateStr.indexOf("to") + 3);

            Date fromDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(fromDateStr);
            Date toDate = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse(toDateStr);

            Date curDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(curDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (dasaFound) {
                cal.add(Calendar.YEAR, 5);
                curDate = cal.getTime();
            }


            if ((fromDate.before(curDate) || fromDate.equals(curDate)) && (toDate.after(curDate) || toDate.equals(curDate))) {
                tableCount++;

                dasaFound = true;

                PdfPCell cell = new PdfPCell();
                cell.setColspan(9);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                Paragraph paragraph = new Paragraph(dasaDetails[i][0] + " (Antaradasa dates are starting dates for each planet)", new Font(this.baseFont, HoraStrings.EXTRA_MEDIUM_FONT_SIZE + 2, 0, BaseColor.BLACK));
                paragraph.setAlignment(1);
                cell.addElement(paragraph);
                table.addCell(cell);

                for (int j = 0; j < this.dasaDetails[i].length - 1; j++) {
                    addCellToDasaTable(table, i, j);
                }
            } else {
                if (dasaFound) {
                    break;
                }
            }
        }

        if (tableCount > 1) {
            dasaHasMoreTables = true;
        }
        return table;
    }

    private void addCellToDasaTable(PdfPTable table, int dasaIndex, int antarDasaIndex) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(this.dasaDetails[dasaIndex][(antarDasaIndex + 1)], new Font(this.baseFont, HoraStrings.EXTRA_MEDIUM_FONT_SIZE + 2, 0, BaseColor.BLACK));
        paragraph.setAlignment(1);
        cell.addElement(paragraph);
        table.addCell(cell);
    }


    private void addMetaDataToPDF(String nativeName) {
        this.document.addTitle("Horoscope of " + nativeName);
        this.document.addSubject("Horoscope cast with Meena 2 Naadi Software");
        this.document.addKeywords("Meena 2");
        this.document.addAuthor("meena2naadi@gmail.com");
        this.document.addCreator("meena2naadi@gmail.com");
    }


    private void addEmptyLinesToPDF(float space) throws Exception {
        Paragraph paragraph = new Paragraph();
        paragraph.setLeading(space);
        document.add(paragraph);
    }

    int pageNumber = 0;

    public void onStartPage(PdfWriter writer, Document document) {
        try {

            this.pageNumber += 1;

            if (this.pageNumber > 1)
                addEmptyLinesToPDF(1);
        } catch (Exception localException) {
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Generated by Meena 2 Naadi Software (meena2naadi@gmail.com) by JKR Astro Research Foundation�"),
                    document.left() + 400, 8, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), 1, new Phrase(String.format("%d", new Object[]{Integer.valueOf(this.pageNumber)})), 765.0F, 20.0F, 0.0F);
        } catch (Exception localException) {
        }
    }

    private PdfPTable addD1ChartToPDF(BirthChart birthChart) throws Exception {
        PdfPTable table = new PdfPTable(4);

        table.setWidthPercentage(20.0F);

        addCellToDChart(12, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(1, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(2, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(3, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(11, table, birthChart.getRaasiChakra(), true);

        addCellToDChart(0, table, birthChart.getRaasiChakra(), false);

        addCellToDChart(4, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(10, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(5, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(9, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(8, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(7, table, birthChart.getRaasiChakra(), true);
        addCellToDChart(6, table, birthChart.getRaasiChakra(), true);

        return table;
    }

    private PdfPTable addD9ChartToPDF(BirthChart birthChart) throws Exception {
        PdfPTable table = new PdfPTable(4);

        table.setWidthPercentage(20.0F);

        addCellToDChart(12, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(1, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(2, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(3, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(11, table, birthChart.getNavamsaChakra(), true);

        addCellToDChart(0, table, birthChart.getNavamsaChakra(), false);

        addCellToDChart(4, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(10, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(5, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(9, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(8, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(7, table, birthChart.getNavamsaChakra(), true);
        addCellToDChart(6, table, birthChart.getNavamsaChakra(), true);

        return table;
    }

    private void addCellToDChart(int houseNumber, PdfPTable table, Chakra chakra, boolean isHouse) throws Exception {
        int i = 0;
        int j = 0;

        PdfPCell cell = new PdfPCell();
        cell.setRowspan(isHouse ? 1 : 2);
        cell.setColspan(isHouse ? 1 : 2);
        cell.setFixedHeight(40.0F);

        if (isHouse) {
//          Rasi rasi = null;
            ArrayList<PlanetInfo> sortedPlanets = new ArrayList<PlanetInfo>();


            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);


            House house = chakra.getHouses().get(houseNumber - 1); //isNavamsa ? this.navamsaRasis[(houseNumber - 1)] : this.rasis[(houseNumber - 1)];
            for (i = 0; i < house.getPlanets().size(); i++) {
                int insertAt = sortedPlanets.size();
                PlanetInfo planetInfo = house.getPlanets().get(i);

                for (j = 0; j < sortedPlanets.size(); j++) {
                    if (((PlanetInfo) sortedPlanets.get(j)).longitude > planetInfo.longitude) {
                        insertAt = j;
                        break;
                    }
                }
                sortedPlanets.add(insertAt, planetInfo);
            }

            StringBuilder planets = new StringBuilder();

            for (i = 0; i < 2; i++) {
                for (j = 0; j < 4; j++) {
                    if (sortedPlanets.size() <= 4 * i + j) {
                        break;
                    }
                    PlanetInfo planet = houseNumber < 9 ? sortedPlanets.get(4 * i + j) : sortedPlanets.get(sortedPlanets.size() - 1 - (4 * i + j));

                    StringBuilder currentPlanet = new StringBuilder();

                    if (planet.index == -1) {
                        currentPlanet.append(HoraStrings.LAGNA).append(" ");
                    } else {
                        if (planet.isRetrograde) {
                            currentPlanet.append("(").append(HoraStrings.PLANET_DISPLAY_NAMES[planet.index]).append(") ");
                        } else {
                            currentPlanet.append(HoraStrings.PLANET_DISPLAY_NAMES[planet.index]).append(" ");
                        }
                    }
                    planets.append(currentPlanet);
                }
            }
            if (planets.length() > 1) {
                planets.deleteCharAt(planets.length() - 1);
            }

            cell.setNoWrap(false);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            String finalPlanets = planets.toString();
            if (finalPlanets.contains("La")) {
                cell.addElement(new Phrase(HoraStrings.LAGNA, new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 5, BaseColor.BLACK)));
                finalPlanets = finalPlanets.replace("La ", "");
                finalPlanets = finalPlanets.replace("La", "");
            }
            cell.addElement(new Phrase(finalPlanets, new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK)));
        } else {


//        	if("Raasi".equalsIgnoreCase(chakra.getChakraName())){
//
//        	}else{
//        		 image = Image.getInstance("meena2.jpg");
//        	}

            Paragraph paragraph = new Paragraph("", new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));
            byte[] imageBytes = images.get(MEENA2_IMAGE);
            if(imageBytes != null) {
                Image image = Image.getInstance(imageBytes);
                image.scaleAbsolute(80.0F, 60.0F);
                paragraph.add(new Chunk(image, 0.0F, 0.0F));
            }
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setFont(new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));

            Paragraph paragraph1 = new Paragraph(chakra.getChakraName(), new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            paragraph1.setFont(new Font(this.baseFont, HoraStrings.LARGE_FONT_SIZE, 0, BaseColor.BLACK));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.addElement(paragraph);
            cell.addElement(paragraph1);
        }

        table.addCell(cell);
    }


}
