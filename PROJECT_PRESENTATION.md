# 铁路售票系统项目介绍

## 1. 项目需求 (Project Requirements)

本项目旨在构建一个功能完善的铁路售票系统（类似 12306），支持用户购票、退票、改签以及管理员对车次、车站、时刻表的管理。

### 核心功能模块

* **用户模块**: 用户注册、登录、个人信息管理。
* **票务模块**: 车票查询、预订、支付、退票、改签。
* **车次管理**: 列车信息管理、车厢管理、座位管理。
* **时刻表管理**: 列车时刻表制定、经停站管理。
* **运营管理**: 车站管理、员工管理、检票（进站/出站）。
* **交易平台**: 二手票交易或票务转让功能 (`ticket_listing`)。

### 技术栈

* **后端**: Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Security, MySQL.
* **前端**: Vue.js, TypeScript, Vite.
* **数据库**: MySQL 8.0+.

---

## 2. 数据库设计 (Database Design)

系统采用关系型数据库 MySQL，主要包含以下核心数据表：

### 基础数据表

* **station (车站表)**: 存储车站的基本信息（代码、名称、城市）。
* **train (列车表)**: 存储列车的基本信息（车次号、列车类型）。
* **coach (车厢表)**: 定义列车的车厢组成、类型及座位数。
* **seat (座位表)**: 定义车厢内的具体座位信息。

### 调度与时刻表

* **train_schedule (列车时刻表)**: 定义某次列车在具体日期的发车计划及状态。
* **schedule_stop (经停站表)**: 定义列车时刻表的具体停靠站点、到达及出发时间。

### 订单与交易

* **user_account (用户表)**: 存储用户信息及角色。
* **booking_order (订单表)**: 记录用户的购票订单及状态（待支付、已支付、已取消等）。
* **seat_allocation (座位分配表)**: 管理具体时刻表的座位占用情况，防止超卖。
* **ticket (车票表)**: 生成的实际乘车凭证，关联订单、座位及乘客。
* **payment (支付表)**: 记录支付流水。
* **ticket_listing (票务挂牌表)**: 用于票务转让或二手交易。

### 运营与检票

* **employee (员工表)**: 车站工作人员信息。
* **checkin (检票记录表)**: 记录乘客进出站的检票日志。

### 数据库模式 (Database Schema)

以下是核心业务表的 SQL 定义：

```sql
-- 车站与列车基础信息
CREATE TABLE station (
  station_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(16) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(128) NOT NULL
);

CREATE TABLE train (
  train_id INT PRIMARY KEY AUTO_INCREMENT,
  train_no VARCHAR(32) NOT NULL UNIQUE,
  train_type VARCHAR(32) NOT NULL
);

-- 时刻表与经停站
CREATE TABLE train_schedule (
  schedule_id INT PRIMARY KEY AUTO_INCREMENT,
  train_id INT NOT NULL,
  depart_date DATE NOT NULL,
  status ENUM('PLANNED', 'OPEN', 'DEPARTED', 'ARRIVED', 'CANCELLED') NOT NULL DEFAULT 'PLANNED',
  CONSTRAINT fk_schedule_train FOREIGN KEY (train_id) REFERENCES train (train_id)
);

CREATE TABLE schedule_stop (
  stop_id INT PRIMARY KEY AUTO_INCREMENT,
  schedule_id INT NOT NULL,
  station_id INT NOT NULL,
  seq_no INT NOT NULL,
  arrival_time TIME NULL,
  departure_time TIME NULL,
  CONSTRAINT fk_stop_schedule FOREIGN KEY (schedule_id) REFERENCES train_schedule (schedule_id),
  CONSTRAINT fk_stop_station FOREIGN KEY (station_id) REFERENCES station (station_id)
);

-- 订单与票务
CREATE TABLE booking_order (
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  status ENUM('PENDING', 'PAID', 'CONFIRMED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
  total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user_account (user_id)
);

CREATE TABLE ticket (
  ticket_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  schedule_id INT NOT NULL,
  seat_id INT NOT NULL,
  passenger_name VARCHAR(128) NOT NULL,
  seat_status ENUM('ACTIVE', 'CANCELLED', 'CHECKED_IN') NOT NULL DEFAULT 'ACTIVE',
  price DECIMAL(10, 2) NOT NULL,
  CONSTRAINT fk_ticket_order FOREIGN KEY (order_id) REFERENCES booking_order (order_id),
  CONSTRAINT fk_ticket_schedule FOREIGN KEY (schedule_id) REFERENCES train_schedule (schedule_id),
  CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat (seat_id)
);

-- 座位分配与交易
CREATE TABLE seat_allocation (
  allocation_id INT PRIMARY KEY AUTO_INCREMENT,
  schedule_id INT NOT NULL,
  seat_id INT NOT NULL,
  status ENUM('AVAILABLE', 'LOCKED', 'RESERVED', 'SOLD') NOT NULL DEFAULT 'AVAILABLE',
  reserved_by_order INT NULL,
  CONSTRAINT fk_allocation_schedule FOREIGN KEY (schedule_id) REFERENCES train_schedule (schedule_id),
  CONSTRAINT fk_allocation_seat FOREIGN KEY (seat_id) REFERENCES seat (seat_id)
);

CREATE TABLE ticket_listing (
  listing_id INT PRIMARY KEY AUTO_INCREMENT,
  seller_id INT NOT NULL,
  order_id INT NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CONSTRAINT fk_listing_seller FOREIGN KEY (seller_id) REFERENCES user_account (user_id),
  CONSTRAINT fk_listing_order FOREIGN KEY (order_id) REFERENCES booking_order (order_id)
);
```

---

## 3. 接口文档 (API Documentation)

后端提供 RESTful API 供前端调用，主要接口如下：

### 认证模块 (Auth)

* `POST /api/auth/login`: 用户登录。
* `POST /api/auth/register`: 用户注册。

### 预订模块 (Booking)

* `GET /api/bookings/user/{userId}`: 获取指定用户的订单列表。
* `POST /api/bookings/reserve`: 发起座位预订请求，创建订单。
* `POST /api/bookings/{id}/cancel`: 取消指定订单。

### 车次与时刻表 (Schedule & Train)

* `GET /api/schedules/search`: 根据出发地、目的地和日期查询车次。
* `GET /api/trains/{id}`: 获取列车详情。

### 车站管理 (Station)

* `GET /api/stations`: 获取所有车站列表。
* `POST /api/stations`: 新增车站（管理员）。

---

## 4. 关键功能代码实现 (Key Functional Code)

### 4.1 订单服务 (BookingService)

`BookingService` 负责处理订单的核心业务逻辑，包括创建订单、查询订单和取消订单。

```java
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingOrderRepository bookingOrderRepository;

    // 根据用户查询订单
    public List<BookingOrder> findByUser(UserAccount user) {
        return bookingOrderRepository.findByUser(user);
    }

    // 创建新订单
    public BookingOrder createOrder(BookingOrder order) {
        order.setStatus(OrderStatus.PENDING);
        return bookingOrderRepository.save(order);
    }
  
    // 取消订单逻辑
    public void cancelOrder(Integer orderId) {
        BookingOrder order = bookingOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
      
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
      
        order.setStatus(OrderStatus.CANCELLED);
        bookingOrderRepository.save(order);
      
        // TODO: 释放关联的座位资源 (SeatAllocation)
    }
}
```

### 4.2 订单控制器 (BookingController)

`BookingController` 暴露 REST API，处理 HTTP 请求并调用 Service 层。

```java
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingOrder>> getUserBookings(@PathVariable("userId") Integer userId) {
        return userService.findById(userId)
            .map(user -> ResponseEntity.ok(bookingService.findByUser(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reserve")
    public ResponseEntity<BookingOrder> reserveSeats(@Valid @RequestBody ReserveSeatsRequest request) {
        BookingOrder order = new BookingOrder();
        // 实际逻辑中需要从 Request DTO 转换并校验座位可用性
        return ResponseEntity.ok(bookingService.createOrder(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") Integer id) {
        bookingService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
```

### 4.3 时刻表服务 (ScheduleService)

`ScheduleService` 负责列车时刻表的创建、查询以及座位资源的初始化。

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final TrainScheduleRepository trainScheduleRepository;
    // ... 其他 Repository 注入

    // 创建时刻表，同时初始化经停站、车厢和座位资源
    public void createSchedule(CreateScheduleRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(train);
        schedule.setDepartDate(request.getDepartDate());
        schedule.setStatus(ScheduleStatus.PLANNED);
        trainScheduleRepository.save(schedule);

        // 创建经停站 (Stops)
        for (ScheduleStopRequest stopRequest : request.getStops()) {
            ScheduleStop stop = new ScheduleStop();
            stop.setSchedule(schedule);
            // ... 设置站点信息
            scheduleStopRepository.save(stop);
        }

        // 创建车厢 (Coaches) 和 座位 (Seats) 及 初始分配状态 (Allocations)
        for (CoachRequest coachRequest : request.getCoaches()) {
            Coach coach = new Coach();
            // ... 设置车厢信息
            coachRepository.save(coach);

            for (int i = 1; i <= coachRequest.getSeatCount(); i++) {
                Seat seat = new Seat();
                // ... 设置座位信息
                seatRepository.save(seat);

                // 初始化座位分配状态为 AVAILABLE
                SeatAllocation allocation = new SeatAllocation();
                allocation.setSchedule(schedule);
                allocation.setSeat(seat);
                allocation.setStatus(SeatAllocationStatus.AVAILABLE);
                seatAllocationRepository.save(allocation);
            }
        }
    }

    // 根据日期范围查询时刻表
    public List<TrainSchedule> findByDateRange(LocalDate start, LocalDate end) {
        return trainScheduleRepository.findByDepartDateBetween(start, end);
    }
}
```

### 4.4 票务交易服务 (TradingService)

`TradingService` 实现了特色的二手票交易功能，允许用户转让已支付的订单。

```java
@Service
@RequiredArgsConstructor
public class TradingService {

    private final TicketListingRepository ticketListingRepository;
    private final BookingOrderRepository bookingOrderRepository;
    // ... 其他 Repository 注入

    // 发布票务转让信息
    @Transactional
    public void createListing(Integer userId, Integer orderId, BigDecimal price) {
        BookingOrder order = bookingOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 校验：只能转让自己的、已支付的订单
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only list your own orders");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Only paid orders can be listed");
        }

        TicketListing listing = new TicketListing();
        listing.setSeller(order.getUser());
        listing.setBookingOrder(order);
        listing.setPrice(price);
        listing.setStatus("ACTIVE");
      
        ticketListingRepository.save(listing);
    }

    // 购买转让票务
    @Transactional
    public void buyListing(Integer buyerId, Integer listingId) {
        TicketListing listing = ticketListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // 校验：不能购买自己发布的、必须是 ACTIVE 状态
        if (!"ACTIVE".equals(listing.getStatus())) {
            throw new RuntimeException("Listing is not active");
        }
        if (listing.getSeller().getId().equals(buyerId)) {
            throw new RuntimeException("You cannot buy your own listing");
        }

        UserAccount buyer = userAccountRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // 1. 更新挂牌状态为 SOLD
        listing.setStatus("SOLD");
        ticketListingRepository.save(listing);

        // 2. 转移订单所有权
        BookingOrder order = listing.getBookingOrder();
        order.setUser(buyer);
        bookingOrderRepository.save(order);

        // 3. 更新车票上的乘客姓名 (可选)
        List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
        for (Ticket ticket : tickets) {
            ticket.setPassengerName(buyer.getName());
            // ... 保存更新
        }
    }
}
```

### 4.5 安全配置 (Security Implementation)

`SecurityConfig` 使用 Spring Security 6.x 的 `SecurityFilterChain` 进行配置，实现了无状态的 REST API 安全策略。

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 配置 CORS 策略，允许前端跨域访问
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 禁用 CSRF，因为使用 Token 认证
            .csrf(AbstractHttpConfigurer::disable)
            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll() // 开放认证接口
                .anyRequest().permitAll() // 开发阶段暂时允许所有请求，生产环境需改为 authenticated()
            );
      
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 允许前端开发服务器
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
      
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 5. 前端实现 (Frontend Implementation)

前端使用 Vue 3 + TypeScript + Element Plus 构建，实现了现代化的单页应用体验。

### 5.1 选座与预订页面 (Booking.vue)

实现了可视化的选座功能，用户可以直接点击座位图进行选择。

```typescript
// 核心逻辑：加载座位并按车厢分组
const seatsByCoach = computed(() => {
  const groups: Record<string, SeatAllocation[]> = {}
  allAllocations.value.forEach(a => {
    const coachNo = a.seat.coach.coachNo
    if (!groups[coachNo]) {
      groups[coachNo] = []
    }
    groups[coachNo].push(a)
  })
  return groups
})

// 加载座位数据
onMounted(async () => {
  try {
    allAllocations.value = await getSeatAllocations(scheduleId)
  } catch (error) {
    ElMessage.error('Failed to load seats')
  } finally {
    loading.value = false
  }
})
```

### 5.2 交易平台页面 (TradingPlatform.vue)

展示了二手票交易列表，并提供了购买和取消挂牌的操作。

```typescript
// 购买票务逻辑
const handleBuy = async (listing: TicketListing) => {
  if (!userStore.user) {
    ElMessage.warning('Please login first');
    return;
  }

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to buy this ticket for ¥${listing.price}?`,
      'Confirm Purchase',
      { confirmButtonText: 'Buy', cancelButtonText: 'Cancel' }
    );

    await buyListing(listing.listingId, userStore.user.id);
    ElMessage.success('Ticket purchased successfully!');
    fetchListings(); // 刷新列表
  } catch (error) {
    // 处理错误
  }
};
```

### 5.3 车次查询页面 (ScheduleSearch.vue)

提供车次查询功能，并展示查询结果，支持查看经停站信息。

```typescript
// 查询逻辑
const onSearch = async () => {
  if (!searchForm.date) {
    ElMessage.warning('Please select a date')
    return
  }
  try {
    const data = await getSchedules({
      date: searchForm.date,
      trainNo: searchForm.trainNo || undefined
    })
    schedules.value = data
  } catch (error) {
    console.error(error)
  }
}

// 查看经停站
const viewStops = async (scheduleId: number) => {
  try {
    const data = await getScheduleStops(scheduleId)
    currentStops.value = data
    stopsDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}
```

### 5.4 订单管理页面 (MyOrders.vue)

用户可以查看自己的订单历史，并进行取消订单或转卖车票的操作。

```typescript
// 取消订单
const handleCancel = (orderId: number) => {
  ElMessageBox.confirm(
    'Are you sure you want to cancel this order?',
    'Warning',
    { confirmButtonText: 'Yes', cancelButtonText: 'No', type: 'warning' }
  ).then(async () => {
    try {
      await cancelBooking(orderId)
      ElMessage.success('Order cancelled')
      refreshOrders() // 刷新列表
    } catch (error) {
      ElMessage.error('Failed to cancel order')
    }
  })
}

// 转卖车票 (发布到交易平台)
const handleSell = (orderId: number) => {
  ElMessageBox.prompt('Please enter the selling price', 'Sell Ticket', {
    confirmButtonText: 'List for Sale',
    cancelButtonText: 'Cancel',
    inputPattern: /^\d+(\.\d{1,2})?$/,
    inputErrorMessage: 'Invalid Price'
  }).then(async ({ value }) => {
    try {
      await createListing({ orderId, price: Number(value) })
      ElMessage.success('Ticket listed for sale')
    } catch (error) {
      ElMessage.error('Failed to list ticket')
    }
  })
}
```
