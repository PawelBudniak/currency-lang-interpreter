# Język zapytań Cypher

## Wstęp
Bazy grafowe to jeden z rodzajów baz NoSQL. Przechowują dane w postaci grafu składającego się z węzłów, przy czym między węzłami można definiować skierowane związki. Węzły mogą przechowywać wiele atrybutów, reprezentowanych przed Cypherowe typy [wbudowane](https://neo4j.com/docs/cypher-manual/current/syntax/values/#property-types).
Baza Neo4j jest określana jako _schema optional_ -  to znaczy można dynamicznie tworzyć węzły o niezdefniowanych wcześniej typach, ale można również nakładać ograniczenia i indeksy na atrybuty kiedy zostanie to uznane za korzystne.

Cypher to deklaratywny język zapytań, odpowiednik SQL dla grafowej bazy Neo4j. Cypher różni się nieco składnią od SQL, ale została ona stworzona tak, aby była łatwa do wizualnej interpretacji, więc można szybko się jej nauczyć. Na przykład poniższe zapytanie zwróci tych ludzi, którzy lubią jedzenie o nazwie "Placki".
 
  ```
   MATCH (p: Person) -[:LIKES] -> (Food {name: "Placki"})  
   RETURN p;
   ```

## Stosowane konwencje nazewnictwa
Wielkość liter ma znaczenie w języku Cypher, dlatego najlepiej od samego początku tworzenia bazy przestrzegać reguł nazewnictwa:


| Byt grafowy  | Konwencja Nazewnictwa | Przykład
| ------------- | ------------- | ------------- |
| Nazwa węzła  | CamelCase |BigDictionary  |
| Nazwa związku  | Wielkie litery, rozdzielane podkreśleniem  |IS_RELATED
| Nazwa atrybutu | camelCase | dictionaryName |
| Zmienna w zapytaniu| Małe literly, rozdzielane podkreśleniem | best_dictionary |


## Przykładowe zapytania

Rozpatrzmy bardziej szczegółowo zapytanie podobne do tego ze wstępu, ale tym razem pochodzące z dziedziny naszego słownika grafowego.
Konstrukcje w nawiasach okrągłych oznaczają węzły. Jeśli chcielibyśmy dopasować jakikolwiek węzeł bez względu na właściwości i typ to napisalibyśmy puste nawiasy: ().  
`e: Entry` oznacza definicje zmiennej e trzymającej takie węzły typu Entry, które spełniają warunek zdefiniowany w MATCH.
`-[:DEFINES]->` wskazuje na związek typu DEFINES wychodzący z Entry do Word. Jeśli nie wiemy jaki jest kierunek związku albo nie interesuje nas to, możemy po prostu zamiast strzałki napisać kolejny myślnik   (`-[DEFINES]--`), wtedy Cypher dopasuje po prostu jakikolwiek związek między węzłami spełniający podane warunki, bez względu na kierunek. (Przy dodawaniu związków do bazy mimo wszystko trzeba zawsze podać jakiś kierunek, nawet jeśli informacja o nim nie będzie wykorzystywana).
`Word {word: "Placki"}` oznacza węzeł typu Word, o atrybucie word równym "Placki". Tutaj nie przypisaliśmy mu żadnej zmiennej, ponieważ nie będzie on dalej wykorzystywany.

    MATCH (e: Entry) -[:DEFINES] -> (Word {word: "Placki"})  
    RETURN e;

Związkom również można przypisać zmienną:

    MATCH (e: Entry) -[def:DEFINES] -> (w:Word {word: "Placki"})  
    RETURN e, def ,w;
Dzięki temu można zwrócić  bardziej rozbudowany graf, zawierający węzły Entry, Word, i krawędzie DEFINES między nimi. Możliwość przypisania związków zmiennej jest o tyle istotne, że jeśli pominiemy ":" na początku nazwy typu związku, to zostanie on po prostu zinterpretowany jako zmienna i dopasuje każdy typ związku, więc warto zwracać na to uwagę.

Ponadto związki też mogą mieć atrybuty (ale nie jest to wykorzystywane w naszym schemacie bazy): 
`-[d:DEFINES {lastUpdated: 2017}]-> `

## Cypher w Springu i naszym słowniku grafowym

Cypher jest wykorzystywany na najniższym poziomie naszych aplikacji w postaci natywnych zapytań do bazy Neo4j. Jest to funkcjonalność udostępniana przez SpringData przy pomocy adnotacji @Query.
```
@RepositoryRestResource(collectionResourceRel = "dictionaries", path="dictionaries")  
public interface DictionaryRepository extends Repository<Dictionary, String>  
{
	@Query("MATCH d: Dictionary{dictionaryName: $name} RETURN d)")  
	Dictionary dictByNameNoEntries(@Param("name") String name);
}
```
Wystarczy napisać deklarację takiej funkcji w interfejsie dziedziczącym po jednym ze Springowych repozytoriów, nie trzeba dostarczać żadnej implementacji, można z niej normalnie korzystać: 
`Dictionary d = dictionaryRepository.dictByNameNoEntries("Medical dictionary")`
Jak widać można przekazywać parametry do zapytań natywnych przy pomocy operatora "$" i adnotacji @Param. Jest to metoda odporna na ataki typu SQL injection, ponieważ Springowa implementacja "escapuje" znaki specjalne z parametrów.

W kolejnym przykładzie przedstawione jest zapytanie pobierające wpis, definiujący podane słowo ze słownika o podanej nazwie, wraz z wychodzącymi z niego związkami i węzłami nimi połączonymi. Wpis w naszym schemacie posiada wiele rodzajów związków wychodzących:  
 - DEFINES - ze słowem _Word_
 - MEANS - z definicjami _Definition_
 - CATEGORIZES - ze swoimi wpisami podrzędnymi _Entry_
```
@Query(" MATCH(Dictionary{dictionaryName: $dictionaryName}) -[:INCLUDES]-> (e: Entry)\n" +  
        "MATCH (e)-[:DEFINES]->(w: Word{word : $word})\n" +  
        "MATCH (e)-[rel]->(target)\n" +  
        "RETURN e, collect(rel), collect(target)")  
Entry findByWord(@Param("word") String word, @Param("dictionaryName") String dictionaryName);
```
Pierwsze dwie instrukcje MATCH znajdują odpowiedno wpisy należące do słownika o podanej nazwie, i wpisy definiujące podane słowo.
Trzecia linijka odpowiada za znalezienie wszystkich związków (zmienna _rel_) i odpowiadającym im węzłów powiązanych (zmienna _target_). Warto zauważyć że nie jest wskazany typ związku ani węzła, więc zostaną dopasowane ich wszystkie rozdaje.
Ostatnia linijka wykorzystuje procedurę _collect_. Jest to wymagane aby Spring Data mógł spodziewać się wielu wartości _rel_ i _target_, stworzyć obiekt Entry i wypełnić atrybuty typu List odpowiednimi wartościami. Dla jasności tak wygląda (skrócona) definicja klasy Entry:
```
@Node  
public class Entry  
{  
  @Id @GeneratedValue  
  private Long id;  
  
  @Relationship(type="DEFINES", direction = Relationship.Direction.OUTGOING)  
  private Word word;  
  
  @Relationship(type="MEANS", direction = Relationship.Direction.OUTGOING)  
  private List<Definition> definitions = new ArrayList<>();  
  
  @Relationship(type="CATEGORIZES", direction = Relationship.Direction.OUTGOING)  
  private List<Entry> subentries = new ArrayList<>();
  // ...
}
```
Ostatnim przykładem będzie zapytanie tworzące nowe obiekty w bazie, w tym wypadku odpowiada za dodanie wpisu "korzenia", czyli takiego który nie ma żadnego rodzica (przychodzącego związku _INCOMING_ ).
Parametrami są: 
 - inputWord - definiowane słowo
 - inputDefinition - jego definicja
 - dictionary - nazwa słownika
```
@Query("" +  
        "MATCH (dict: Dictionary {dictionaryName: $dictionary} )" +  
        "MERGE(word:Word{word: $inputWord})\n" +  
        "CREATE(definition: Definition{definition: $inputDefinition})\n" +  
        "CREATE((entry:Entry) -[:MEANS]-> (definition) ) " +  
        "MERGE( (entry) -[:DEFINES]-> (word) )"+  
        "MERGE( (dict) -[:INCLUDES]-> (entry) )"  
)  
void defineRootEntry(@Param("inputWord") String inputWord, @Param("inputDefinition") String inputDefinition,  
  @Param ("dictionary") String dictionary);
```
` "MERGE(word:Word{word: $inputWord})\n"  `
Działa tak, że jeśli istnieje już węzeł Word z takim atrybutem word, to nie zostanie utworzony nowy obiekt i zmienna _word_ będzie wskazywać na ten już istniejący. W przeciwnym wypadku zostanie utworzony nowy węzeł Word. Warto zaznaczyć, że przy dużych rozmiarach bazy takie przeszukiwanie przy każdym dodawaniu może okazać się kosztowne czasowo.

`"CREATE(definition: Definition{definition: $inputDefinition})\n" + `
Instrukcja CREATE tworzy nowy węzeł bez sprawdzania czy taki już istnieje.

Ostatnie dwie linijki wykorzystują wcześniej określone zmienne _entry_, _word_, _dict_, i zapisują do bazy związki między nimi przy pomocy instrukcji MERGE, działającej analogicznie dla związków jak dla węzłów.

## Podsumowanie
Przedstawione zostały podstawowe funkcjonalności języka Cypher, za pomocą którego można tworzyć zapytania do grafowej bazy Neo4j. Cypher może zostać wykorzystany w aplikacji korzystającej ze Spring Framework poprzez tak zwane zapytanie natywne definiowane w adnotacji @Query.
