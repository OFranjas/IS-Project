# Pontos para Relatório

## Server-side

Falar do delay

## Client-Side

### Ideia por trás de cada query

- Em todas as queries utilizamos um StringBuilder para guardar os resultados obtidos para depois serem escritos num ficheiro de texto.
- Criou-se a classe FileOutputUtil para se escrever os resultados num ficheiro de texto quando o fluxo de dados é completado com sucesso. Esta classe também contém um método para limpar o conteúdo de um ficheiro de texto, que é utilizado no início de cada query para garantir que o ficheiro está vazio.
- Para o cálculo do tempo de execução utilizou-se a classe Instant da biblioteca java.time e a classe Duration para calcular a diferença entre dois Instant. O tempo de execução apenas é calculado quando a query é completada com sucesso.
- De notar também que o método then() foi utilizado para ignorar a emissão de elementos e apenas reagir ao sinal de conclusão. Para além disso, faz com que se retorne um Mono<Void> uma vez que os dados são guardados num ficheiro de texto e não são emitidos para o cliente.

1. ***Names and telephones of all Owners***

   - Nesta query processa-se um Flux de objetos do tipo Owner. Para cada um deles, guarda-se o correspondente nome e número de telefone.
   - Quando toda a lista é processada com sucesso o buffer é escrito para um ficheiro de texto e é calculado também o tempo de execução da query. 
   - Caso ocorra algum erro, informa-se o utilizador do mesmo.

2. ***Total number of Pets***

   - Processando um FLux de objetos do tipo Pet, utiliza-se o método count() da classe Flux é possível obter o número total de objetos no Flux, obtendo-se assim um Mono<Long> com o resultado desejado.

3. ***Total number of dogs***

   - Ao Fluxo de Pets aplicamos um filtro para obtermos um novo Flux apenas com Pets da espécie "dog". De seguida contamos o número de elementos de uma forma semelhante à query anterior, através do método count(). 

4. ***Total number of animals weighting more than 10 kg. Sort this list by ascending order of animal weight.***

   - Inicialmente filtramos os Pets para obtermos apenas os que têm um peso superior a 10 kg através do método filter().
   - Seguidamente, utilizamos o método sort() para ordenar os Pets por ordem crescente de peso. De forma a definir o comparador, utilizou-se a biblioteca Comparator com o método comparingDouble().

5. ***Average and standard deviations of animal weights***

   - Primeiramente calcula-se os valores necessários para o cálculo da média e do desvio padrão, nomeadamente o número total de Pets, a soma dos pesos de todos os Pets e a soma do quadrado dos pesos.
   - Tanto para a soma dos pesos como para a soma do quadrado dos pesos mapeou-se para cada Pet o respetivo peso e de seguida utilizou-se o método reduce() para obter a soma correspondente. Para o número total de Pets utilizou-se apenas o método count().
   - Para juntarmos estes resultados num único Mono constituído por um tuplo com os 3 valores, utilizou-se o método zip().
   - A esse Mono aplicou-se um flatMap() onde se calcula a média e o desvio padrão através das fórmulas matemáticas correspondentes, transformando o Mono original num Mono vazio no caso de sucesso ou num Mono error no caso de não haverem Pets na base de dados.

6. ***The name of the eldest Pet***

   - Nesta query apenas aplicamos um reduce() ao Flux de Pets. Neste reduce vamos sequencialmente comparando a *birth date* de dois Pets e guardando o que tiver a data mais antiga. No final, o Mono resultante contém o Pet mais velho.
  
7. ***Average number of Pets per Owner, considering only owners with more than one 
animal***

   - Starting with a collect() to put all the elements of the Pets Flux in a HashMap<Long, Long>, being the key the id of the Owner and the value the number of Pets of that Owner. The merge adds 1 to the count for each pet or increments the count if the Owner already exists in the HashMap.
   - Then we flatten the resulting map to a Mono<String> using a flatMap(). Inside it we calculate the total number of pets and owners. Then we can compute the average and append it to the resultBuffer.

8. ***Name  of  Owner  and  number  of  respective  Pets,  sorted  by  this  number  in 
descending  order***

   - Começa por utilizar o getPetsIdsByOwnerId() para cada owner num flatMap() para obter um Flux<Long> com os ids dos Pets de cada Owner. 
    - Utiliza-se o método count() para obter o número de Pets de cada Owner, mapeando o resultado para um Mono<Tuple2<Long, Long>> com o id do Owner e o número de Pets.
   - Para ordenar os Owners por ordem decrescente de número de Pets, utilizou-se o método sort() comparando o número de Pets de cada Owner.
   - Após ter ordenado os Owners, foi-se guardando os valores dos tuplos num doOnNext() para depois serem escritos num ficheiro de texto.

9. ***The same as before but now with the names of all pets instead of simply the 
number***

- Utilizou-se inicialmente um flatMap() para obter um Flux dos ids dos Pets de cada Owner para de seguida se utilizar outro flatMap() para obter um Flux dos Pets com os ids obtidos anteriormente. Logo de seguida, através de um map(), tranformou-se cada Pet no seu respetivo nome.
- Com essa informação, faz-se um reduce() para colocar todos os nomes dos Pets numa lista. Com essa lista de nomes, mapeamos cada dono com a respetiva lista de nomes através do map().
- Tendo os tuplos com o id do Owner e a lista de nomes dos Pets, utilizou-se o método sort() para ordenar os Owners por ordem decrescente de número de Pets, comparando o tamanho da lista de nomes de cada Owner.
- Após essa ordenação apenas restava guardar os valores, que foi feito num doOnNext().

### Otimizações

Para otimizar o código de forma a que este corresse mais rapidamente utilizámos threads, de forma a que cada query corresse numa thread diferente. 
Para tal retornámos Mono<void> em todas as queries (daí a utilização do then() em todas as queries) e combinámos todas as queries num único Mono. Utilizámos um block() para esperar que todas as queries terminassem antes de terminar o programa do cliente.

### Aprendizagens com o projeto

- Aprendemos a utilizar o Spring WebFlux e a programar reativamente.
- Ganhámos uma maior noção de como funcionam os Fluxos e Monos e de que forma os podemos manipular.

### Comparar resultados de performance

#### Fazer sem o collect no 7 e meter relatorio a comparar tempo

- Diferença entre 5000ms e 900ms

Código sem collect:
```
return allOwners
                .flatMap(owner -> petService.getPetIdsByOwnerId(owner.getIdentifier())
                        .count()
                        .filter(count -> count > 1) // Only consider owners with more than one pet
                        .map(count -> Tuples.of(owner, count))) // Map to a tuple of owner and count
                .reduce(new HashMap<Long, Long>(), (acc, tuple) -> {
                    // Accumulate counts in a map
                    acc.put(tuple.getT1().getIdentifier(), tuple.getT2());
                    return acc;
                })
                .flatMap(map -> {
                    long totalPets = map.values().stream().mapToLong(Long::longValue).sum();
                    long totalOwners = map.size();

                    double average = totalOwners == 0 ? 0 : (double) totalPets / totalOwners;
                    resultBuffer.append("Average number of pets per owner (more than one pet): ")
                            .append(String.format("%.2f", average))
                            .append("\n");

                    return Mono.just(resultBuffer.toString());
                })
                .doOnSuccess(content -> FileOutputUtil.writeToFile(filePath, resultBuffer.toString()))
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error computing average: " + error.getMessage());
                })
                .doFinally(signalType -> {
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 7: ✅ -> " + duration.toMillis() + " ms");
                    // Perform any final actions here, if needed
                })
                .then(); // Return an empty Mono<Void> to complete the chain
```


#### Falar da cache()

Ao utilizar-se cache() para guardar os Fluxos allOwners e allPets em memória criou-se um problema de concorrência, uma vez que os Fluxos são partilhados por todas as queries. Para evitar este problema não se utilizou o cache() e apenas se guardou numa variável o pedido para os fluxos, servindo apenas para melhor organização do código.

