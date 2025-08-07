package minimizacaoMapaK;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Classe para ler e processar arquivos no formato PLA.
 */
public class LeitorPLA {

    /**
     * Conta o número de linhas de dados válidas em um arquivo PLA.
     * Ignora comentários (#), linhas vazias e diretivas (.i, .o, .e).
     * @param nomeArquivo O caminho para o arquivo PLA.
     * @return O número de linhas de dados válidas.
     */
    public static int contaLinha(String nomeArquivo) {
        int cont = 0;
        
        try (Scanner sc = new Scanner(new File(nomeArquivo))) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine().trim();
                if (!linha.isEmpty() && !linha.startsWith("#") && !linha.startsWith(".")) {
                    cont++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao contar linhas do arquivo: " + e.getMessage());
            return -1;
        }
        
        return cont;
    }

    /**
     * Lê o conteúdo de um arquivo PLA em um array de strings.
     * @param nomeArquivo O caminho para o arquivo PLA.
     * @param numLinha O número de linhas de dados a serem lidas.
     * @return Um array de strings contendo as linhas de dados do arquivo.
     */
    public static String[] lerArquivo(String nomeArquivo, int numLinha) {
        String[] linhas = new String[numLinha];
        int i = 0;
        
        try (Scanner sc = new Scanner(new File(nomeArquivo))) {
            while (sc.hasNextLine() && i < numLinha) {
                String linha = sc.nextLine().trim();
                if (!linha.isEmpty() && !linha.startsWith("#") && !linha.startsWith(".")) {
                    linhas[i++] = linha;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            return null;
        }
        
        return linhas;
    }

    /**
     * Obtém o número de entradas e saídas do arquivo PLA a partir das diretivas .i e .o.
     * @param nomeArquivo O caminho para o arquivo PLA.
     * @return Um array de inteiros onde o primeiro elemento é o número de entradas e o segundo é o número de saídas.
     */
    public static int[] inOutArquivo(String nomeArquivo) {
        int[] inOut = new int[2];
        boolean hasI = false, hasO = false;

        try (Scanner sc = new Scanner(new File(nomeArquivo))) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine().trim();
                if (linha.startsWith(".i ")) {
                    inOut[0] = Integer.parseInt(linha.substring(3).trim());
                    hasI = true;
                } else if (linha.startsWith(".o ")) {
                    inOut[1] = Integer.parseInt(linha.substring(3).trim());
                    hasO = true;
                }

                if (hasI && hasO) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo para entradas/saídas: " + e.getMessage());
        }

        if (!hasI || !hasO) {
            throw new IllegalArgumentException("Arquivo PLA inválido: faltam diretivas .i ou .o");
        }

        return inOut;
    }

    /**
     * Extrai os minterms para uma determinada saída.
     * @param lista O conteúdo do arquivo PLA.
     * @param in O número de entradas.
     * @param escolhaSaida O índice da saída a ser considerada.
     * @return Um array de inteiros com os minterms.
     */
    public static int[] funcValoresVdd(String[] lista, int in, int escolhaSaida) {
        List<Integer> valores = new ArrayList<>();
        
        for (String linha : lista) {
            if (linha == null) continue;
            
            String[] partes = linha.split(" ");
            
            if (partes.length >= 2 && partes[0].length() == in && partes[1].length() > escolhaSaida) {
                char saida = partes[1].charAt(escolhaSaida);
                if (saida == '1') {
                    try {
                        String binario = partes[0].replace('-', '0');
                        int valor = Integer.parseInt(binario, 2);
                        valores.add(valor);
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter valor binário: " + partes[0]);
                    }
                }
            }
        }
        
        return valores.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Extrai os "don't cares" para uma determinada saída.
     * @param lista O conteúdo do arquivo PLA.
     * @param in O número de entradas.
     * @param escolhaSaida O índice da saída a ser considerada.
     * @return Um array de inteiros com os don't cares.
     */
    public static int[] funcDontCares(String[] lista, int in, int escolhaSaida) {
        List<Integer> dontCares = new ArrayList<>();
        
        for (String linha : lista) {
            if (linha == null) continue;
            
            String[] partes = linha.split(" ");
            
            if (partes.length >= 2 && partes[0].length() == in && partes[1].length() > escolhaSaida) {
                char saida = partes[1].charAt(escolhaSaida);
                if (saida == '-') {
                    try {
                        String binario = partes[0].replace('-', '0');
                        int valor = Integer.parseInt(binario, 2);
                        dontCares.add(valor);
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter valor binário: " + partes[0]);
                    }
                }
            }
        }
        
        return dontCares.stream().mapToInt(Integer::intValue).toArray();
    }
}
