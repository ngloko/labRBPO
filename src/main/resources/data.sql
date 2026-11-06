-- Тестовые данные. Скрипт идемпотентен: повторный запуск не создаёт дубликатов.
-- Демонстрационные учётные записи: admin/admin123 (ADMIN), curator/curator123 (CURATOR).

INSERT INTO app_user (id, username, password_hash, name, role) VALUES
    (1, 'admin', '$2a$10$WVsWlnxeXLrnnqn9eWLzSOxplk7wu8ufdcjwwIybTTIC1OMhS.yOy', 'Администратор музея', 'ADMIN'),
    (2, 'curator', '$2a$10$jt5FTrOyzUJuy58koWbXz.UXuRRBmkAjI7e3d9oDwWLblO8tM1hku', 'Иванова Анна Сергеевна', 'CURATOR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO exhibition_hall (id, name, area, is_available) VALUES
    (1, 'Зал Античности', 120.00, TRUE),
    (2, 'Зал Живописи', 80.00, TRUE),
    (3, 'Зал Техники', 50.00, FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO exhibit (id, inventory_number, name, condition, required_area) VALUES
    (1, 'INV-001', 'Древнегреческая амфора', 'GOOD', 2.50),
    (2, 'INV-002', 'Скифский золотой гребень', 'EXCELLENT', 1.00),
    (3, 'INV-003', 'Картина «Утро в сосновом лесу»', 'DAMAGED', 6.00),
    (4, 'INV-004', 'Модель паровоза', 'RESTORATION', 4.00),
    (5, 'INV-005', 'Фрагмент фрески', 'GOOD', 3.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO exhibition (id, name, start_date, end_date, status, curator_id) VALUES
    (1, 'Сокровища древности', now() - interval '1 day', now() + interval '30 days', 'PLANNED', 2),
    (2, 'Ретро-техника', now() - interval '10 days', now() + interval '20 days', 'ACTIVE', 2),
    (3, 'Зимняя сказка', now() - interval '60 days', now() - interval '30 days', 'COMPLETED', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO exhibit_placement (id, exhibit_id, exhibition_id, hall_id, status, placed_at, removed_at) VALUES
    (1, 1, 2, 1, 'INSTALLED', now() - interval '10 days', NULL),
    (2, 3, 2, 1, 'INSTALLED', now() - interval '10 days', NULL),
    (3, 5, 1, 2, 'PLANNED', now() - interval '1 day', NULL),
    (4, 2, 3, 1, 'REMOVED', now() - interval '60 days', now() - interval '30 days')
ON CONFLICT (id) DO NOTHING;

-- После вставки с явными id выравниваем последовательности identity-колонок.
SELECT setval(pg_get_serial_sequence('app_user', 'id'), COALESCE((SELECT MAX(id) FROM app_user), 1), TRUE);
SELECT setval(pg_get_serial_sequence('exhibition_hall', 'id'), COALESCE((SELECT MAX(id) FROM exhibition_hall), 1), TRUE);
SELECT setval(pg_get_serial_sequence('exhibit', 'id'), COALESCE((SELECT MAX(id) FROM exhibit), 1), TRUE);
SELECT setval(pg_get_serial_sequence('exhibition', 'id'), COALESCE((SELECT MAX(id) FROM exhibition), 1), TRUE);
SELECT setval(pg_get_serial_sequence('exhibit_placement', 'id'), COALESCE((SELECT MAX(id) FROM exhibit_placement), 1), TRUE);
