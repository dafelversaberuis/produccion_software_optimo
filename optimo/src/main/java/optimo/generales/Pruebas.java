package optimo.generales;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

public class Pruebas {

	public static void main(String[] args) {
		try {

			LocalDateTime localDateTime1 = LocalDateTime.of(2017, 03, 05, 10, 30);
			LocalDateTime localDateTime2 = LocalDateTime.of(2017, 03, 07, 17, 00);
			Duration duration = Duration.between(localDateTime1, localDateTime2);

			long milisegundos = duration.toMillis();
			
			
			BigDecimal horas = new BigDecimal(milisegundos / 3600000.00);
			System.out.println(horas);
		} catch (Exception e) {

		}
		// // TODO Auto-generated method stub
		// try {
		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Date fechaInicial;
		//
		// fechaInicial = dateFormat.parse("2017-12-30");
		//
		//// Date fechaFinal = dateFormat.parse("2017-03-01");
		////
		//// int dias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) /
		// 86400000);
		////
		//// System.out.println("Hay " + dias + " dias de diferencia");
		//
		// ConsultarFuncionesAPI c = new ConsultarFuncionesAPI();
		//
		// System.out.println(c.getFechaDiasSumados(fechaInicial, 3));
		//
		//
		//
		// } catch (
		//
		// ParseException e) {
		// // TODO Auto-generated catch block
		//
		// }

	}

}
