# Wikipedia Statistics

В рамках задания необходимо реализовать многопоточную программу
для подсчёта различной статистики о статьях русской Википедии.

Автор лучшей реализации получит дополнительные очки!

## Исходные данные

XML дампы доступны по ссылке [https://dumps.wikimedia.org/ruwiki/](https://dumps.wikimedia.org/ruwiki/)
Для основного задания нужно использовать дампы *текущего состояния статей* в папке `20211101`.

Список файлов, которые надо скачать и обработать можно получить командой

```(bash)
curl -s https://dumps.wikimedia.org/ruwiki/20211101/dumpstatus.json | jq -r '.jobs.metacurrentdump.files | to_entries[] | "https://dumps.wikimedia.org\(.value | .url)"' | sort

https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current1.xml-p1p224167.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current2.xml-p224168p1042043.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current3.xml-p1042044p2198269.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current4.xml-p2198270p3698269.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current4.xml-p3698270p3835772.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current5.xml-p3835773p5335772.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current5.xml-p5335773p6585765.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current6.xml-p6585766p8085765.bz2
https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current6.xml-p8085766p8560833.bz2

```

Т.о. надо скачать и обработать файлы `https://dumps.wikimedia.org/ruwiki/20211101/ruwiki-20211101-pages-meta-current2.xml-p224168p1042043.bz2` и т.д.
Файлы представляют собой xml файл сжатый bzip2.
Для распаковки bzip2 и анализа файлов воспользуйтесь одной из свободно доступных библиотек.
Для анализа статей рекомендуется использовать потоковый (SAX) парсер XML.

Пример статьи (тэг `page`):

```(xml)
<mediawiki
    xmlns="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/
    http://www.mediawiki.org/xml/export-0.10.xsd" version="0.10" xml:lang="ru">
  ...
  <page>
    <title>Базовая статья</title>
    <ns>0</ns>
    <id>4</id>
    <redirect title="Заглавная страница" />
    <restrictions>sysop</restrictions>
    <revision>
      <id>237414</id>
      <parentid>22491</parentid>
      <timestamp>2004-08-09T04:36:57Z</timestamp>
      <contributor>
        <username>Maximaximax</username>
        <id>450</id>
      </contributor>
      <comment>redir</comment>
      <model>wikitext</model>
      <format>text/x-wiki</format>
      <text bytes="49" xml:space="preserve">#REDIRECT [[Заглавная страница]]</text>
      <sha1>fv75zcmgjvd2h4rn3jqc0yrlf07afkh</sha1>
    </revision>
  </page>
  ...   
```

### Статистика

Русскими будем считать слова, состоящие из трёх и более русских букв - `[а-яA-Z]{3,}`. Всё остальное - разделители.
При сборе статистики слова должны сравниваться без учёта регистра (case insensitive).
Разные формы слова считаем разными словами.

1. Топ-300 русских слов наиболее часто встречающихся в названиях статей. `page/title`
   При равном количестве упорядочить по алфавиту.

   Формат:

    ```
    Топ-300 слов в заголовках статей:
    <Число вхождений><пробел><Слово>
    ...
    ```

2. Топ-300 русских слов наиболее часто встречающихся в самих статьях. `page/revision/text`
   При равном количестве упорядочить по алфавиту.

   Формат:

    ```
    Топ-300 слов в статьях:
    <Число вхождений><пробел><Слово>
    ...
    ```

3. Распределение статей по размеру (логарифмическая шкала) `page/revision/text@bytes`

   Формат:

    ```
    Распределение статей по размеру:
    0<пробел><Количество статей размером 0-9 байт>
    1<пробел><Количество статей размером 10-99 байт>
    2<пробел><Количество статей размером 100-999 байт>
    ...
    ```

   В первой и последней строке не должно быть количество 0.
   В остальных строках может быть 0, если подходящих статей нет.

4. Распределение статей по времени `page/revision/timestamp`

   Формат:

    ```
    Распределение статей по времени:
    <год YYYY><пробел><Количество статей в году>
    ...
    ```
   В первой и последней строке не должно быть количество 0.
   В остальных строках может быть 0, если подходящих статей нет.

#### Пример
XML на входе

```(xml)
<mediawiki xml:lang="ru">
    <page>
        <title>Простой заголовок</title>
        <revision>
            <timestamp>2021-11-11T14:14:14Z</timestamp>
            <text bytes="26">Простой текст</text>
        </revision>
    </page>
</mediawiki>
```

Результат на выходе

```
Топ-300 слов в заголовках статей:
1 заголовок
1 простой

Топ-300 слов в статьях:
1 простой
1 текст

Распределение статей по размеру:
0 0
1 1

Распределение статей по времени:
2021 1

```
### Требования к реализации

Необходимо использовать заданное число потоков для работы (параметр `--threads`).
Программа должна принимать на вход один или несколько bzip2-файлов (параметры `--input`), разделённых запятыми.
Программа должна записывать в файл отчёт со статистикой (параметр `--output`).

Если на вход подан не bzip2-файл, должно бросаться исключение.
Если на внутри bzip2-файла находится не единственный well-formed XML, должно бросаться исключение.
Если для сбора статистики в статье не хватает данных (например, нет атрибута `bytes` у тега `text`),
статья не должна учитываться ни в каких статистиках, её нужно игнорировать.

### Тесты

Необходимо реализовать unit-тесты для парсера статей и подсчёта статистики.
