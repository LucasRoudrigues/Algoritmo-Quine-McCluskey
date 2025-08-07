package minimizacaoMapaK;

import java.util.List;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // O caminho do arquivo precisa ser atualizado para o seu sistema.
        String nomeArquivo = "C:\\Users\\Kaender\\Downloads\\benchmark\\benchmark\\ex00.train.pla"; // Nome do arquivo PLA para o teste.

        try {
            // 1. Ler o arquivo PLA e obter o número de entradas e saídas
            int[] inOut = LeitorPLA.inOutArquivo(nomeArquivo);
            int in = inOut[0];  // número de variáveis de entrada
            int out = inOut[1]; // número de saídas

            // 2. Ler o conteúdo de dados do arquivo PLA
            int linhas = LeitorPLA.contaLinha(nomeArquivo);
            String[] conteudo = LeitorPLA.lerArquivo(nomeArquivo, linhas);

            if (conteudo == null) {
                System.out.println("Não foi possível ler o arquivo.");
                return;
            }
            
            // 3. Processar cada saída
            for (int saida = 0; saida < out; saida++) {
                System.out.println("\nProcessando saída " + saida + "...");
                
                // Obter minterms e don't cares para esta saída
                int[] minterms = LeitorPLA.funcValoresVdd(conteudo, in, saida);
                int[] dontCares = LeitorPLA.funcDontCares(conteudo, in, saida);
                
                System.out.println("Minterms encontrados: " + Arrays.toString(minterms));
                System.out.println("Don't cares encontrados: " + Arrays.toString(dontCares));

                // 4. Minimizar com Quine-McCluskey
                QuineMcCluskey qm = new QuineMcCluskey(in, minterms, dontCares);
                List<String> resultado = qm.minimize();

                // 5. Mostrar resultados
                System.out.println("Expressão minimizada para a saída " + saida + ":");
                if (resultado.isEmpty()) {
                    System.out.println("  Sem termos na expressão minimizada.");
                } else {
                    for (String bin : resultado) {
                        System.out.println("  " + QuineMcCluskey.toExpression(bin));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
