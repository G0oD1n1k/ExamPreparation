<h1>1.	Способы создания и запуска потока. </h1>

<h1>2.	Способ создания и запуска потока на основе расширения класса Thread. Пример.</h1>

<h1>3.	Способ создания и запуска потока на основе реализации интерфейса Runnable. Пример.</h1>

<h1>4.	Управление потоками. Жизненный цикл потока.</h1>
<p>Приостановить (задержать) выполнение потока можно с помощью метода <code>sleep(int millis)</code> класса <code>Thread</code>.
<br>Менее надежный альтернативный способ состоит в вызове метода <code>yield()</code>,
<br>который может сделать некоторую паузу и позволяет другим потокам начать выполнение своей задачи. 
<br>Метод <code>join()</code> блокирует работу потока, в котором он вызван, до тех пор,
<br>пока не будет закончено выполнение вызывающего метод потока или не истечет время ожидания при обращении к методу <code>join(long timeout)</code>.</p>

```Java
class JoinThread extends Thread {
    
    public JoinThread(String name) {
        super(name);
    }
    
    public void run() {
        String nameThread = getName();
        long timeout = 0;
        System.out.printf("Старт потока: %s%n", nameThread);
        
        try {
            switch (nameThread) {
                case "First": {
                    timeout = 5_000;
                }
                case "Second": {
                    timeout = 1_000;
                }
            }
            Thread.sleep(timeout);
            System.out.printf("Завершение потока: %s%n", nameThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class JoinRunner {
    static {
        System.out.println("Старт потока main");
    }
    public static void main(String[] args) {
        JoinThread thread1 = new JoinThread("First");
        JoinThread thread2 = new JoinThread("Second");
        thread1.start();
        thread2.start();
        try {
            thread1.join(); // Поток Main остановлен до окончания работы потока thread1
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Название текущего потока потока: %s%n", Thread.currentThread().getName());
    }
}
```
<p>Несмотря на вызов метода <code>join()</code> для потока <code>thread1</code>, поток <code>thread2</code> будет работать,
<br>в отличие от потока <code>main</code>, который сможет продолжить свое выполнение только по завершении потока <code>thread1</code></p>

<p>Вызов статического метода <code>yield()</code> для исполняемого потока должен приводить к приостановке потока на некоторый квант времени,
<br>чтобы другие потоки могли выполнять свои действия. Например, в случае потока с высоким приоритетом после обработки части пакета данных, когда следующая еще не готова,
<br>стоит уступить часть времени другим потокам. Однако если требуется надежная остановка потока, то следует использовать его крайне осторожно или вообще применить другой способ.</p>

```Java
public class YieldRunner {
    public static void main(String[] args) {
        new Thread() {
            public void run() {
                System.out.println("Старт потока 1");
                Thread.yield();
                System.out.println("Завершение потока 1");
            }
        }.start();

        new Thread() {
            public void run() {
                System.out.println("Старт потока 2");
                System.out.println("Завершение потока 2");
            }
        }.start();
    }
}
```
<p>В результате может быть выведено:
<br><strong>Старт потока 1</strong>
<br><strong>Cтарт потока 2</strong>
<br><strong>Завершение потока 2</strong>
<br><strong>Завершение потока 1</strong>
<br>Активизация метода <code>yield()</code> в коде метода <code>run()</code> первого объекта потока приведет к тому, 
<br>что, скорее всего, первый поток будет остановлен на некоторый квант времени, что даст возможность другому потоку запуститься и выполнить свой код. </p>
<h1>5.	Управление приоритетами и группами потоков.</h1>
<p>Потоку можно назначить приоритет от 1 (константа MIN_PRIORITY) до 10 (MAX_PRIORITY) с помощью метода: <code>setPriority(int prior)</code>
<br>Получить значение приоритета потока также можно при помощи метода:<code>getPriority()</code></p>
<p>Пример:</p>

```Java
public class PriorThread extends Thread {
    public PriorThread(String name) {
        super(name);
    }
    public void run() {
        for (int i = 0; i < 7; i++) {
            System.out.println(getName() + " " + i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.err.print(e);
            }
        }
    }
}
public class PriorityRunner {
    public static void main(String[] args) {
        PriorThread min = new PriorThread("Min");
        PriorThread max = new PriorThread("Max");
        PriorThread norm = new PriorThread("Norm");
        
        min.setPriority(Thread.MIN_PRIORITY);
        max.setPriority(Thread.MAX_PRIORITY);
        norm.setPriority(Thread.NORM_PRIORITY);
        
        min.start();
        max.start();
        norm.start();
    }
}
```
<p>Поток с более высоким приоритетом в данном случае, как правило, монополизирует вывод на консоль.
<br>Потоки объединяются в группы потоков. После создания потока нельзя изменить его принадлежность к группе.</p>

```Java
ThreadGroup threadGroup = new ThreadGroup("Группа потоков 1");
Thread thread = new Thread(threadGroup, "Поток 0");
```
<p>Все потоки, объединенные в группы, имеют одинаковый приоритет. Чтобы определить, к какой группе относится поток, следует вызвать метод:</p>

```Java
getThreadGroup();
```
<p>Если поток до включения в группу имел приоритет выше приоритета группы потоков, то после включения значение его приоритета станет равным приоритету группы.
<br>Поток же со значением приоритета, более низким, чем приоритет группы после включения в новую, значение своего приоритета не изменит.</p>

<h1>6.	Потоки-демоны.</h1>
<p>Потоки-демоны используются для работы в фоновом режиме вместе с программой, но не являются неотъемлемой частью логики программы. 
<br>Если какой-либо процесс может выполняться на фоне работы основных потоков выполнения и его деятельность заключается в обслуживании основных потоков
<br>приложения, то такой процесс может быть запущен как поток-демон. С помощью метода <code>setDaemon(boolean value)</code>, вызванного вновь созданным потоком до его запуска, 
<br>можно определить поток-демон. Метод <code>boolean isDaemon()</code> позволяет определить, является ли указанный поток демоном или нет.</p>

```Java
public class SimpleThread extends Thread {
    public void run() {
        try {
            if (isDaemon()) {
                System.out.println("Старт потока-демона.");
                Thread.sleep(10_000);
            } else {
                System.out.println("Старт обычного потока.");
            }
        } catch (InterruptedException e) {
            System.err.print(e);
        } finally {
            if (!isDaemon()) {
                System.out.println("Завершение обычного потока.");
            } else {
                System.out.println("Завершение потока-демона.");
            }
        }
    }
}

public class DaemonRunner {
    public static void main(String[] args) {
        SimpleThread usual = new SimpleThread();
        SimpleThread daemon = new SimpleThread();
        daemon.setDaemon(true);
        usual.start();
        daemon.start();
        System.out.println("Последний оператор main");
    }
}
```
<p>
В результате компиляции и запуска, возможно, будет выведено:
<br><strong>Последний оператор main</strong>
<br><strong>Старт потока-демона</strong>
<br><strong>Старт обычного потока</strong>
<br><strong>Завершение обычного потока</strong>
<br>Поток-демон (из-за вызова метода <code>sleep(10000)</code>) не успел завершить выполнение своего кода до завершения основного потока приложения, 
<br>связанного с методом <code>main()</code>. Базовое свойство потоков-демонов заключается в возможности основного потока приложения 
<br>завершить выполнение потока-демона (в отличие от обычных потоков) с окончанием кода метода <code>main()</code>, не обращая внимания на то, 
<br>что поток-демон еще работает. Если уменьшать время задержки потока-демона, то он может успеть завершить свое выполнение до окончания работы основного потока.
</p>

<h1>7.	Методы и инструкции (блок кода) synchronized.</h1>
<h2>Синхронизация методов</h2>
<p>Синхронизированный метод изолирует объект, после чег он становится доступным для других потоков.
<br>Изоляция снимается, когда поток полностью выполнит соответствующий метод.
<br>Другой способ снятия изоляции - вызов метода <code>wait()</code> из изолированного метода. </p>

```Java
import java.io.FileWriter;
import java.io.IOException;

public class Resource {
    private FileWriter fileWriter;

    public Resource(String file) throws IOException {
        fileWriter = new FileWriter(file, true);
    }
    public synchronized void writing(String str, int i) {
        try {
            fileWriter.append(str + i);
            System.out.print(str + i);
            Thread.sleep((long)Math.random() * 50);
            fileWriter.append("->" + i + " ");
            System.out.print("->" + i + " ");
        } catch (IOException e) {
            System.err.print("Ошибка файла: " + e);
        } catch (InterruptedException e) {
            System.err.print("Ошибка потока: " + e);
        }
    }
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.err.print("Ошибка закрытия файла: " + e);
        }
    }
}
public class SyncThread extends Thread {
    private Resource resource;
    public SyncThread(String name, Resource rs) {
        super(name);
        this.resource = rs;
    }
    public void run() {
        for (int i = 0; i < 5; i++) {
            resource.writing(getName(), i);
        }
    }
}
public class SynchroRun {
    public static void main(String[] args) {
        Resource resource = null;
        try {
            resource = new Resource("data\\result.txt");
            SyncThread thread1 = new SyncThread("First", resource);
            SyncThread thread2 = new SyncThread("Second", resource);
            thread1.start();
            thread2.start();
        } catch (IOException e) {
            System.err.print("Ошибка файла: " + e);
        } catch (InterruptedException e) {
            System.err.print("Ошибка потока: " + e);
        } finally {
            resource.close();
        }
    }
}
```

<p>Код построен таким образом, что при отключении синхронизации метода <code>writing()</code> в случае его вызова одним потоком другой поток может вклиниться
<br>и произвести запись своей информации, несмотря на то, что метод не завершил запись, инициированную первым потоком.</p>
<h2>Синхронизация блоков кода</h2>
<p>
<br>В этом случае происходит блокировка объекта, указанного в инструкции <code>synchronized</code>, и он становится недоступным для других синхронизированных
<br>методов и блоков. Такая синхронизация позволяет сузить область синхронизации, т. е. вывести за пределы синхронизации код, в ней не нуждающийся.
<br>Обычные методы на синхронизацию внимания не обращают, поэтому ответственность за грамотную блокировку объектов ложится на программиста.
</p>
<h1>8.	Контроль за доступом к объекту-ресурсу (монитор). Методы wait(), notify() и notifyAll().</h1>
<p>Реализация паттерна Producer-Consumer с использованием низкоуровневых методов синхронизации:</p>

```Java
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
private final Queue<Integer> buffer = new LinkedList<>();
private final int CAPACITY = 5;
private final Object lock = new Object();

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                // Ждем, пока в буфере есть место
                while (buffer.size() == CAPACITY) {
                    System.out.println("Буфер полон. Производитель ждет...");
                    lock.wait();
                }
                
                System.out.println("Произведено: " + value);
                buffer.add(value++);
                
                // Уведомляем потребителей, что появились данные
                lock.notifyAll();
                
                // Имитация обработки
                Thread.sleep(300);
            }
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                // Ждем, пока в буфере появятся данные
                while (buffer.isEmpty()) {
                    System.out.println("Буфер пуст. Потребитель ждет...");
                    lock.wait();
                }
                
                int value = buffer.poll();
                System.out.println("Съедено: " + value);
                
                // Уведомляем производителей, что появилось место
                lock.notifyAll();
                
                // Имитация обработки
                Thread.sleep(500);
            }
        }
    }

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();
        
        Thread producerThread = new Thread(() -> {
            try { pc.produce(); } 
            catch (InterruptedException e) { e.printStackTrace(); }
        });
        
        Thread consumerThread = new Thread(() -> {
            try { pc.consume(); } 
            catch (InterruptedException e) { e.printStackTrace(); }
        });
        
        producerThread.start();
        consumerThread.start();
    }
}
```
<p><strong>Объяснение:</strong> В этом примере реализован классический паттерн Producer-Consumer. 
Производитель (produce) генерирует значения и помещает их в буфер, а потребитель (consume) извлекает их. 
Когда буфер полон, производитель переходит в состояние ожидания через <code>wait()</code>. 
Когда потребитель извлекает элемент, он вызывает <code>notifyAll()</code> для пробуждения производителя. 
Аналогично, когда буфер пуст, потребитель ждет, а производитель уведомляет его после добавления нового элемента.</p>

<h1>9.	Пакет java.util.concurrent. Способы управления потоками.</h1>

```Java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        executor.execute(() -> System.out.println("Задача 1 выполняется"));
        executor.execute(() -> System.out.println("Задача 2 выполняется"));
        
        executor.shutdown();
    }
}
```

<h1>10.	Пакет java.util.concurrent. Перечисление TimeUnit.</h1>

```Java
import java.util.concurrent.TimeUnit;

public class TimeUnitExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Начали выполнение");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Прошло 2 секунды");
        
        long hours = TimeUnit.DAYS.toHours(2);
        System.out.println("2 дня = " + hours + " часов");
    }
}
```

<h1>11.	Объекты синхронизации. Блокирующие очереди. Пример.</h1>

<p>Использование ArrayBlockingQueue для многопоточного логирования:</p>

```Java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogSystem {
    private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(100);
    private volatile boolean running = true;

    public void start() {
        // Запуск потребителя логов в отдельном потоке
        Thread consumerThread = new Thread(() -> {
            while (running || !logQueue.isEmpty()) {
                try {
                    String log = logQueue.take();
                    System.out.println("[LOG] " + log);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public void stop() {
        running = false;
    }

    public void log(String message) {
        try {
            // Неблокирующая попытка добавления в очередь
            if (!logQueue.offer(message)) {
                System.err.println("Очередь логов переполнена! Сообщение потеряно: " + message);
            }
        } catch (Exception e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LogSystem logSystem = new LogSystem();
        logSystem.start();
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // Генерация логов из разных потоков
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    logSystem.log("Задача " + taskId + " - Сообщение " + j);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
        logSystem.stop();
    }
}
```
<p><strong>Объяснение:</strong> Этот пример демонстрирует использование блокирующей очереди для реализации системы логирования. 
Множество рабочих потоков отправляют логи в очередь, а отдельный поток-потребитель извлекает сообщения и выводит их. 
Использование <code>offer()</code> позволяет избежать блокировки при переполнении очереди, 
а <code>take()</code> гарантирует, что потребитель будет ждать новые сообщения.</p>

<h1>12.	Объекты синхронизации. Семафоры. Пример.</h1>
<p>Ограничение доступа к внешнему API с помощью семафора:</p>

```Java
import java.util.concurrent.Semaphore;

public class ApiRateLimiter {
    private final Semaphore semaphore;

    public ApiRateLimiter(int maxRequests) {
        this.semaphore = new Semaphore(maxRequests);
    }
    
    public String callApi(String endpoint) throws InterruptedException {
        semaphore.acquire();  // Получаем разрешение
        
        try {
            // Имитация вызова API
            Thread.sleep(200);
            return "Ответ от " + endpoint;
        } finally {
            semaphore.release();  // Всегда освобождаем разрешение
        }
    }
    
    public static void main(String[] args) {
        ApiRateLimiter limiter = new ApiRateLimiter(3);  // Макс 3 параллельных запроса
        
        // Создаем 10 потоков, имитирующих запросы к API
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    String endpoint = "/api/data/" + threadId;
                    System.out.println("Поток " + threadId + " начал запрос к " + endpoint);
                    String response = limiter.callApi(endpoint);
                    System.out.println("Поток " + threadId + " получил ответ: " + response);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```
<p><strong>Объяснение:</strong> Семафор используется для ограничения количества одновременных вызовов внешнего API. 
Каждый поток должен получить разрешение (<code>acquire()</code>) перед выполнением запроса и освободить его 
(<code>release()</code>) после завершения, даже в случае ошибки. Это предотвращает перегрузку внешнего сервиса 
и обеспечивает соблюдение лимитов запросов.</p>
<h1>13.	Объекты синхронизации. Барьеры CycleBarrier. Пример.</h1>

```Java
import java.util.concurrent.*;

public class Auction {
    private static final int BIDS_NUMBER = 3;
    private final List<Integer> bids = new ArrayList<>();
    private final CyclicBarrier barrier;

    public Auction() {
        this.barrier = new CyclicBarrier(BIDS_NUMBER, () -> {
            // Определение победителя после сбора всех ставок
            int winner = bids.stream().max(Integer::compare).orElse(0);
            System.out.println("\nВсе ставки приняты! Победитель: " + winner);
        });
    }

    public boolean placeBid(int bid) throws InterruptedException, BrokenBarrierException, TimeoutException {
        synchronized (bids) {
            if (bids.size() >= BIDS_NUMBER) {
                return false; // Лимит ставок достигнут
            }
            bids.add(bid);
            System.out.println("Ставка " + bid + " принята");
        }

        // Ожидаем других участников с таймаутом
        barrier.await(30, TimeUnit.SECONDS);
        return true;
    }

    public static void main(String[] args) {
        Auction auction = new Auction();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Создаем клиентов с разными ставками
        for (int i = 1; i <= 5; i++) {
            final int clientId = i;
            executor.submit(() -> {
                try {
                    int bid = 100 * clientId;
                    if (!auction.placeBid(bid)) {
                        System.out.println("Клиент " + clientId + ": Аукцион завершен, ставка не принята");
                    }
                } catch (Exception e) {
                    System.out.println("Клиент " + clientId + ": Ошибка - " + e.getMessage());
                }
            });
        }
        executor.shutdown();
    }
}
```
<p><strong>Объяснение:</strong> В этом примере:</p>
<ul>
  <li>Аукцион использует <code>CyclicBarrier</code> с размером барьера <code>BIDS_NUMBER</code></li>
  <li>Каждый клиент пытается сделать ставку через метод <code>placeBid()</code></li>
  <li>Метод <code>await()</code> с таймаутом предотвращает deadlock при неполучении нужного количества ставок</li>
  <li>После сбора всех ставок автоматически запускается поток определения победителя</li>
  <li>Синхронизированный блок гарантирует корректное добавление ставок</li>
  <li>"Лишние" клиенты получают уведомление о завершении аукциона</li>
</ul>

<h1>14.	Объекты синхронизации. «Щеколда» CountDownLatch. Пример.</h1>

```Java
import java.util.concurrent.*;

public class GradingSystem {
    static class Student {
        private final String name;
        private final CountDownLatch latch;
        private final int[] grades;
        
        public Student(String name, int assignmentsCount) {
            this.name = name;
            this.latch = new CountDownLatch(assignmentsCount);
            this.grades = new int[assignmentsCount];
        }
        
        public void submitAssignment(int assignmentId, int grade) {
            grades[assignmentId] = grade;
            System.out.println(name + " сдал задание " + (assignmentId+1));
        }
        
        public void waitForGrades() throws InterruptedException {
            latch.await(); // Ожидаем проверки всех заданий
            double average = calculateAverage();
            System.out.printf("%s: Средняя оценка = %.2f%n", name, average);
        }
        
        private double calculateAverage() {
            int sum = 0;
            for (int grade : grades) sum += grade;
            return (double) sum / grades.length;
        }
        
        public void gradeAssignment(int assignmentId, int grade) {
            grades[assignmentId] = grade;
            latch.countDown(); // Уменьшаем счетчик
            System.out.println("Задание " + (assignmentId+1) + " оценено на " + grade);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int ASSIGNMENTS = 4;
        Student student = new Student("Иван Петров", ASSIGNMENTS);
        ExecutorService executor = Executors.newFixedThreadPool(ASSIGNMENTS);
        
        // Студент сдает задания
        for (int i = 0; i < ASSIGNMENTS; i++) {
            final int id = i;
            executor.submit(() -> {
                student.submitAssignment(id, 0); // Оценка пока неизвестна
                try {
                    Thread.sleep(300); // Время на выполнение
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Преподаватель проверяет задания
        for (int i = 0; i < ASSIGNMENTS; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(500); // Время на проверку
                    int grade = ThreadLocalRandom.current().nextInt(3, 6); // Случайная оценка 3-5
                    student.gradeAssignment(id, grade);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Студент ожидает результаты
        executor.submit(() -> {
            try {
                student.waitForGrades();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
```
<p><strong>Объяснение:</strong></p>
<ul>
  <li>Студент создает <code>CountDownLatch</code> с количеством, равным числу заданий</li>
  <li>После сдачи всех заданий студент вызывает <code>await()</code> и блокируется</li>
  <li>Преподаватель проверяет каждое задание и вызывает <code>countDown()</code></li>
  <li>Когда все задания проверены (счетчик = 0), студент разблокируется и вычисляет среднюю оценку</li>
  <li>Система гарантирует, что средняя оценка будет вычислена только после проверки всех работ</li>
</ul>

<h1>15.	Объекты синхронизации. Обмен блокировками Exchanger. Пример.</h1>

```Java
import java.util.concurrent.*;

public class Market {
    static class Producer implements Runnable {
        private final Exchanger<int[]> exchanger;
        private int production = 1000;
        
        public Producer(Exchanger<int[]> exchanger) {
            this.exchanger = exchanger;
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    int[] productionData = {production, 0}; // [произведено, продано]
                    Thread.sleep(1500);
                    
                    System.out.println("Производитель: текущее производство " + production);
                    int[] marketData = exchanger.exchange(productionData);
                    
                    int sales = marketData[1];
                    System.out.println("Производитель: получены данные о продажах " + sales);
                    
                    // Корректировка производства
                    if (production > sales) {
                        production = Math.max(500, production - 200);
                        System.out.println("Производитель: снижаю производство до " + production);
                    } else {
                        production += 200;
                        System.out.println("Производитель: увеличиваю производство до " + production);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    static class Consumer implements Runnable {
        private final Exchanger<int[]> exchanger;
        private int sales = 800;
        private int price = 100;
        
        public Consumer(Exchanger<int[]> exchanger) {
            this.exchanger = exchanger;
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    int[] salesData = {0, sales}; // [произведено, продано]
                    Thread.sleep(2000);
                    
                    System.out.println("Потребитель: текущие продажи " + sales);
                    int[] productionData = exchanger.exchange(salesData);
                    
                    int production = productionData[0];
                    System.out.println("Потребитель: получены данные о производстве " + production);
                    
                    // Корректировка цены
                    if (production > sales) {
                        price = Math.max(50, price - 10);
                        System.out.println("Потребитель: снижаю цену до " + price);
                    } else {
                        price += 10;
                        System.out.println("Потребитель: повышаю цену до " + price);
                    }
                    
                    // Обновление продаж
                    sales = 800 + ThreadLocalRandom.current().nextInt(-100, 101);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) {
        Exchanger<int[]> exchanger = new Exchanger<>();
        new Thread(new Producer(exchanger)).start();
        new Thread(new Consumer(exchanger)).start();
    }
}
```
<p><strong>Объяснение:</strong></p>
<ul>
  <li>Производитель и потребитель обмениваются данными через <code>Exchanger</code></li>
  <li>Производитель отправляет данные о производстве, получает данные о продажах</li>
  <li>Потребитель отправляет данные о продажах, получает данные о производстве</li>
  <li>На основе полученных данных:
    <ul>
      <li>Производитель корректирует объем производства</li>
      <li>Потребитель корректирует цену на товар</li>
    </ul>
  </li>
  <li>Обмен происходит синхронно - оба потока блокируются до завершения обмена</li>
</ul>

<h1>16.	Объекты синхронизации. Альтернатива synchronized. Интерфейсы Lock. Пример.</h1>

```Java
import java.util.*;
import java.util.concurrent.locks.*;

public class PairCollection {
    private final List<String> items = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    
    public void addPair(String item1, String item2) {
        lock.lock();
        try {
            items.add(item1);
            items.add(item2);
            System.out.println("Добавлена пара: [" + item1 + ", " + item2 + "]");
            System.out.println("Текущее состояние: " + items);
        } finally {
            lock.unlock();
        }
    }
    
    public void removePair() {
        lock.lock();
        try {
            if (items.size() >= 2) {
                String item2 = items.remove(items.size() - 1);
                String item1 = items.remove(items.size() - 1);
                System.out.println("Удалена пара: [" + item1 + ", " + item2 + "]");
                System.out.println("Текущее состояние: " + items);
            } else {
                System.out.println("Недостаточно элементов для удаления пары");
            }
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        PairCollection collection = new PairCollection();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Потоки добавляют пары
        for (int i = 1; i <= 3; i++) {
            final int pairId = i;
            executor.submit(() -> {
                collection.addPair("A" + pairId, "B" + pairId);
            });
        }
        
        // Потоки удаляют пары
        for (int i = 1; i <= 3; i++) {
            executor.submit(collection::removePair);
        }
        
        // Смешанные операции
        executor.submit(() -> collection.addPair("X1", "Y1"));
        executor.submit(collection::removePair);
        executor.submit(() -> collection.addPair("X2", "Y2"));
        
        executor.shutdown();
    }
}
```
<p><strong>Объяснение:</strong></p>
<ul>
  <li>Класс использует <code>ReentrantLock</code> для обеспечения атомарности операций с парами</li>
  <li>Метод <code>addPair()</code> добавляет два элемента как единую неделимую операцию</li>
  <li>Метод <code>removePair()</code> удаляет два последних элемента как единую операцию</li>
  <li>Блокировка гарантирует, что между добавлением/удалением элементов одной пары не произойдет вмешательства других потоков</li>
  <li>Использование <code>try-finally</code> гарантирует освобождение блокировки даже при возникновении исключений</li>
  <li>Вывод состояния коллекции после каждой операции демонстрирует целостность данных</li>
</ul>

<h1>17.	Объекты синхронизации. Класс ExecutorService и интерфейс Callable. Пример.</h1>

<p>Параллельная обработка задач с возвратом результатов и обработкой ошибок:</p>

```Java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelProcessor {
    private final ExecutorService executor;

    public ParallelProcessor(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public List<Future<Integer>> processTasks(List<Future<Integer>> tasks) {
        return tasks.stream()
                .map(executor::submit)
                .toList();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        List<Callable> tasks = new ArrayList<>();

        // Создаем разнотипные задачи
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            tasks.add(() -> {
                if (taskId % 4 == 0) {
                    throw new RuntimeException("Искусственная ошибка в задаче " + taskId);
                }

                // Имитация полезной работы
                Thread.sleep(500);
                return taskId * 10;
            });
        }

        ParallelProcessor processor = new ParallelProcessor(4);
        List<Future<Integer>> futures = processor.processTasks(tasks);

        // Обработка результатов
        for (int i = 0; i < futures.size(); i++) {
            try {
                Integer result = futures.get(i).get();
                System.out.println("Задача " + i + " успешно завершена. Результат: " + result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                System.err.println("Ошибка в задаче " + i + ": " + e.getCause().getMessage());
            }
        }

        processor.shutdown();
    }
}
```

<p><strong>Объяснение:</strong> В этом примере показано использование <code>ExecutorService</code> 
и <code>Callable</code> для параллельной обработки задач с возвратом результатов. 
Особенности реализации:
<br>1. Задачи могут возвращать результаты
<br>2. Обработка исключений через <code>ExecutionException</code>
<br>3. Использование пула потоков для ограничения параллелизма
<br>4. Гарантированное завершение работы через <code>shutdown()</code></p>

<h1>18.	Объекты синхронизации. Класс Phaser. Пример. </h1>
<p>Многофазная обработка данных с динамическим изменением числа участников:</p>

```Java
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class DataProcessingPipeline {
    static class ProcessingTask implements Runnable {
        private final Phaser phaser;
        private final int taskId;
        
        public ProcessingTask(Phaser phaser, int taskId) {
            this.phaser = phaser;
            this.taskId = taskId;
            phaser.register();
        }
        
        @Override
        public void run() {
            try {
                // Фаза 1: Загрузка данных
                System.out.println("Задача " + taskId + ": Загрузка данных");
                Thread.sleep(ThreadLocalRandom.current().nextInt(300));
                phaser.arriveAndAwaitAdvance();
                
                // Фаза 2: Обработка данных
                System.out.println("Задача " + taskId + ": Обработка данных");
                Thread.sleep(ThreadLocalRandom.current().nextInt(500));
                phaser.arriveAndAwaitAdvance();
                
                // Фаза 3: Сохранение результатов
                System.out.println("Задача " + taskId + ": Сохранение результатов");
                Thread.sleep(ThreadLocalRandom.current().nextInt(200));
                
                // Дерегистрация после завершения
                phaser.arriveAndDeregister();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) {
        final int TASKS = 5;
        Phaser phaser = new Phaser(1);  // Основной поток регистрируется
        
        // Запуск задач обработки
        for (int i = 0; i < TASKS; i++) {
            new Thread(new ProcessingTask(phaser, i)).start();
        }
        
        // Ожидание завершения фазы 0 (регистрация всех задач)
        phaser.arriveAndDeregister();
        
        // Мониторинг прогресса
        while (!phaser.isTerminated()) {
            int phase = phaser.getPhase();
            System.out.println("--- Текущая фаза: " + phase + " ---");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Все задачи завершены");
    }
}
```
