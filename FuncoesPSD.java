import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FuncoesPSD {
	
	static public int contaLinha (String nomeArquivo){
		int cont =0;
		
		try{
			File arquivo = new File(nomeArquivo);
            Scanner leitor = new Scanner(arquivo);
            while (leitor.hasNextLine()) {
				cont++;
				leitor.nextLine();
			}
			leitor.close();
			} catch (IOException e) {
            	System.out.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
			}
		return cont;
	}
	
	static public String[] lerArquivo(String nomeArquivo, int numLinha){
		int i =0;
		String []linha = new String [numLinha];
		try {
			File arquivo = new File(nomeArquivo);
            Scanner leitor = new Scanner(arquivo);
            while (leitor.hasNextLine()) {
                linha[i] = leitor.nextLine();
                i++;
			}
		leitor.close();     
		} catch (IOException e) {
            System.out.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
		}
		return linha;
	}
	
		static public int [] inOutArquivo(String lista []){
			int inOut[] = new int [2];
			String firsts;
			for (int i =0; i < lista.length; i++){
				lista[i] = lista[i].trim().replaceAll ("\\s+", " ");
				firsts = lista[i].substring (0,2);
				if (firsts.equals(".i")){
					String inS = lista[i].substring (3);
					inOut[0]= Integer.parseInt (inS);	
				}
				if (firsts.equals(".o")){
					String outS = lista[i].substring (3);
					inOut [1] = Integer.parseInt (outS);
				}	
			}
			return inOut;
		}
		
		static public int [] funcValoresVdd (String lista [], int in, int escolhaSaida ,int numLinhas){
			String first, last;
			int value, cont =0, j=0;
			int [] valoresVddI = new int [numLinhas]; 
			for (int i =0; i < lista.length; i++){
			lista[i] = lista[i].trim().replaceAll("\\s+", "");
			first = lista[i].substring(0,1);
			if (first.equals("0")|| first.equals("1")){
				last = lista[i].substring (in+escolhaSaida, in+escolhaSaida+1);
				value = Integer.parseInt (last);
				if (value == 1){
					valoresVddI[j] = cont;
					j++;
				}
				cont++;
		    }		
		}
		int [] valoresVdd = new int [j];
		for (int i= 0; i<j;i++){
			valoresVdd[i] = valoresVddI[i];
		}
		return valoresVdd;
	}
	}
