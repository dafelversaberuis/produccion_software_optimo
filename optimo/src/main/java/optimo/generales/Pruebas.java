package optimo.generales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pruebas {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fechaInicial;

			fechaInicial = dateFormat.parse("2017-12-30");

//			Date fechaFinal = dateFormat.parse("2017-03-01");
//
//			int dias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 86400000);
//
//			System.out.println("Hay " + dias + " dias de diferencia");
			
			ConsultarFuncionesAPI c = new ConsultarFuncionesAPI();
			
			System.out.println(c.getFechaDiasSumados(fechaInicial, 3));
			
			
			
		} catch (

		ParseException e) {
			// TODO Auto-generated catch block

		}

	}

}
