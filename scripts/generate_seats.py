
import os

def generate_sql():
    sql = []
    sql.append("USE `12306`;")
    sql.append("START TRANSACTION;")

    # Helper to add seat and allocation
    # We need to know the next available IDs. 
    # Since we can't know for sure, we will use a starting offset that is likely safe, or just let AutoIncrement handle it if we omit ID.
    # But for seat_allocation, we need seat_id.
    # So we should probably use variables or just assume a clean state/continuation.
    # Given the seed_data has explicit IDs, we should probably continue from there.
    # Max Seat ID in seed_data is 7.
    # Max Coach ID is 3.
    # Max Allocation ID is 5.
    
    seat_id_counter = 8
    allocation_id_counter = 6
    coach_id_counter = 4

    # --- Schedule 1 (G1001, 2025-12-20) ---
    # Coach 1 (C1, FIRST_CLASS) - Existing ID 1
    # Rows 2-5 (Row 1 partially exists)
    for row in range(1, 6):
        for col in ['A', 'C', 'D', 'F']:
            seat_no = f"{row}{col}"
            # Skip existing
            if seat_no in ['1A', '1B']: continue 
            
            sql.append(f"INSERT INTO seat (seat_id, coach_id, seat_no, seat_class) VALUES ({seat_id_counter}, 1, '{seat_no}', 'FIRST_CLASS');")
            sql.append(f"INSERT INTO seat_allocation (allocation_id, schedule_id, seat_id, status) VALUES ({allocation_id_counter}, 1, {seat_id_counter}, 'AVAILABLE');")
            seat_id_counter += 1
            allocation_id_counter += 1

    # Coach 2 (C2, SECOND_CLASS) - Existing ID 2
    # Rows 1-10
    for row in range(1, 11):
        for col in ['A', 'B', 'C', 'E', 'F']:
            seat_no = f"{row}{col}"
            # Skip existing
            if seat_no in ['5A', '5B', '6A']: continue
            
            sql.append(f"INSERT INTO seat (seat_id, coach_id, seat_no, seat_class) VALUES ({seat_id_counter}, 2, '{seat_no}', 'SECOND_CLASS');")
            sql.append(f"INSERT INTO seat_allocation (allocation_id, schedule_id, seat_id, status) VALUES ({allocation_id_counter}, 1, {seat_id_counter}, 'AVAILABLE');")
            seat_id_counter += 1
            allocation_id_counter += 1

    # --- Schedule 2 (G1001, 2025-12-21) ---
    # Needs Coaches first
    # Coach C1
    sql.append(f"INSERT INTO coach (coach_id, schedule_id, coach_no, coach_type, seat_count) VALUES ({coach_id_counter}, 2, 'C1', 'FIRST_CLASS', 20);")
    c1_id = coach_id_counter
    coach_id_counter += 1
    
    for row in range(1, 6):
        for col in ['A', 'C', 'D', 'F']:
            seat_no = f"{row}{col}"
            sql.append(f"INSERT INTO seat (seat_id, coach_id, seat_no, seat_class) VALUES ({seat_id_counter}, {c1_id}, '{seat_no}', 'FIRST_CLASS');")
            sql.append(f"INSERT INTO seat_allocation (allocation_id, schedule_id, seat_id, status) VALUES ({allocation_id_counter}, 2, {seat_id_counter}, 'AVAILABLE');")
            seat_id_counter += 1
            allocation_id_counter += 1

    # Coach C2
    sql.append(f"INSERT INTO coach (coach_id, schedule_id, coach_no, coach_type, seat_count) VALUES ({coach_id_counter}, 2, 'C2', 'SECOND_CLASS', 50);")
    c2_id = coach_id_counter
    coach_id_counter += 1
    
    for row in range(1, 11):
        for col in ['A', 'B', 'C', 'E', 'F']:
            seat_no = f"{row}{col}"
            sql.append(f"INSERT INTO seat (seat_id, coach_id, seat_no, seat_class) VALUES ({seat_id_counter}, {c2_id}, '{seat_no}', 'SECOND_CLASS');")
            sql.append(f"INSERT INTO seat_allocation (allocation_id, schedule_id, seat_id, status) VALUES ({allocation_id_counter}, 2, {seat_id_counter}, 'AVAILABLE');")
            seat_id_counter += 1
            allocation_id_counter += 1

    sql.append("COMMIT;")
    
    return "\n".join(sql)

if __name__ == "__main__":
    content = generate_sql()
    with open("db/more_seats.sql", "w", encoding="utf-8") as f:
        f.write(content)
    print("Generated db/more_seats.sql")
