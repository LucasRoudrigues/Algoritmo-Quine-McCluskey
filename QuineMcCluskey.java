package minimizacaoMapaK;

import java.util.*;

/**
 * Implementa o algoritmo de Quine-McCluskey para minimização de funções booleanas.
 */
public class QuineMcCluskey {
    private final int numVars;
    private final Set<Integer> minterms;
    private final Set<Integer> dontCares;

    public QuineMcCluskey(int numVars, int[] mintermsArray, int[] dontCaresArray) {
        this.numVars = numVars;
        this.minterms = new HashSet<>();
        this.dontCares = new HashSet<>();
        
        for (int m : mintermsArray) minterms.add(m);
        if (dontCaresArray != null) {
            for (int d : dontCaresArray) dontCares.add(d);
        }
    }

    /**
     * Minimiza a função booleana e retorna a expressão simplificada.
     * @return Uma lista de strings, onde cada string é um termo da expressão minimizada.
     */
    public List<String> minimize() {
        Set<String> primeImplicants = findPrimeImplicants();
        return selectEssentialPrimeImplicants(primeImplicants);
    }

    /**
     * Encontra todos os implicantes primos.
     * @return Um conjunto de strings, cada uma representando um implicante primo em binário.
     */
    private Set<String> findPrimeImplicants() {
        Set<String> primes = new HashSet<>();
        Set<String> allTerms = new HashSet<>();
        
        for (int m : minterms) allTerms.add(toBinary(m));
        for (int d : dontCares) allTerms.add(toBinary(d));

        Set<String> currentTerms = new HashSet<>(allTerms);
        
        boolean changed;
        do {
            changed = false;
            Set<String> nextTerms = new HashSet<>();
            Set<String> used = new HashSet<>();
            
            List<String> termsList = new ArrayList<>(currentTerms);
            for (int i = 0; i < termsList.size(); i++) {
                for (int j = i + 1; j < termsList.size(); j++) {
                    String combined = combine(termsList.get(i), termsList.get(j));
                    if (combined != null) {
                        nextTerms.add(combined);
                        used.add(termsList.get(i));
                        used.add(termsList.get(j));
                        changed = true;
                    }
                }
            }
            
            for (String term : currentTerms) {
                if (!used.contains(term)) {
                    primes.add(term);
                }
            }
            
            currentTerms = nextTerms;
        } while (changed);
        
        // Adiciona os últimos termos que não foram combinados
        primes.addAll(currentTerms);
        
        return primes;
    }

    /**
     * Seleciona os implicantes primos essenciais para cobrir todos os minterms.
     * @param primeImplicants Conjunto de todos os implicantes primos.
     * @return Uma lista de strings com a expressão minimizada.
     */
    private List<String> selectEssentialPrimeImplicants(Set<String> primeImplicants) {
        List<String> result = new ArrayList<>();
        Set<Integer> coveredMinterms = new HashSet<>();
        
        // Identifica implicantes primos essenciais
        for (String pi : primeImplicants) {
            Set<Integer> coveredByPi = getCoveredMinterms(pi);
            int uniqueCoverCount = 0;
            int uniqueMinterm = -1;
            
            for (int m : coveredByPi) {
                if (minterms.contains(m)) {
                    int coverCount = 0;
                    for (String otherPi : primeImplicants) {
                        if (pi.equals(otherPi)) continue;
                        if (getCoveredMinterms(otherPi).contains(m)) {
                            coverCount++;
                        }
                    }
                    if (coverCount == 0) {
                        uniqueCoverCount++;
                        uniqueMinterm = m;
                    }
                }
            }
            
            if (uniqueCoverCount > 0) {
                result.add(pi);
                coveredMinterms.addAll(coveredByPi);
            }
        }
        
        // Remove os minterms já cobertos
        Set<Integer> remainingMinterms = new HashSet<>(minterms);
        remainingMinterms.removeAll(coveredMinterms);
        
        // Se ainda houver minterms para cobrir, adiciona o resto dos implicantes primos.
        // Uma implementação mais completa usaria Petrick's Method, mas esta é uma abordagem mais simples.
        if (!remainingMinterms.isEmpty()) {
            for (String pi : primeImplicants) {
                if (!result.contains(pi)) {
                    Set<Integer> coveredByPi = getCoveredMinterms(pi);
                    if (coveredByPi.stream().anyMatch(remainingMinterms::contains)) {
                        result.add(pi);
                        remainingMinterms.removeAll(coveredByPi);
                    }
                    if (remainingMinterms.isEmpty()) {
                        break;
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * Combina dois termos binários se eles diferem em apenas um bit.
     * @param a O primeiro termo binário.
     * @param b O segundo termo binário.
     * @return O termo combinado ou null se não puderem ser combinados.
     */
    private String combine(String a, String b) {
        int diff = 0;
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                diff++;
                combined.append('-');
            } else {
                combined.append(a.charAt(i));
            }
            if (diff > 1) return null;
        }
        return diff == 1 ? combined.toString() : null;
    }

    /**
     * Converte um número decimal para sua representação binária com um número fixo de bits.
     * @param number O número decimal.
     * @return A string binária.
     */
    private String toBinary(int number) {
        String bin = Integer.toBinaryString(number);
        return String.format("%" + numVars + "s", bin).replace(' ', '0');
    }

    /**
     * Obtém o conjunto de minterms cobertos por um implicante primo.
     * @param pi O implicante primo em formato binário (com hifens).
     * @return Um conjunto de minterms em formato decimal.
     */
    private Set<Integer> getCoveredMinterms(String pi) {
        Set<Integer> covered = new HashSet<>();
        List<Integer> dontCarePositions = new ArrayList<>();
        
        for (int i = 0; i < pi.length(); i++) {
            if (pi.charAt(i) == '-') {
                dontCarePositions.add(i);
            }
        }
        
        // Gera todas as combinações para os hifens
        int numCombinations = (int) Math.pow(2, dontCarePositions.size());
        for (int i = 0; i < numCombinations; i++) {
            StringBuilder temp = new StringBuilder(pi);
            String bin = toBinary(i).substring(numVars - dontCarePositions.size());
            
            for (int j = 0; j < dontCarePositions.size(); j++) {
                temp.setCharAt(dontCarePositions.get(j), bin.charAt(j));
            }
            
            int minterm = Integer.parseInt(temp.toString(), 2);
            covered.add(minterm);
        }
        
        return covered;
    }

    /**
     * Converte um termo binário (com hifens) para uma expressão booleana,
     * usando uma convenção de nomenclatura de variáveis escalável (v0, v1, v2...).
     * @param bin O termo binário.
     * @return A string da expressão booleana.
     */
    public static String toExpression(String bin) {
        StringBuilder expr = new StringBuilder();
        for (int i = 0; i < bin.length(); i++) {
            String varName = "v" + i;
            if (bin.charAt(i) == '0') {
                expr.append(varName).append("'");
            } else if (bin.charAt(i) == '1') {
                expr.append(varName);
            }
        }
        return expr.toString();
    }
}
