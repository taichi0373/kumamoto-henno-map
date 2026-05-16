import pandas as pd
import sys

# コマンドライン引数からフォルダ名を取得
folder_name = sys.argv[1]

# ファイルパスを組み立てる
file_path_stop_times = f'./{folder_name}/stop_times.txt'
file_path_stops = f'./{folder_name}/stops.txt'
file_path_trips = f'./{folder_name}/trips.txt'

# データを読み込む
stop_times = pd.read_csv(file_path_stop_times)
stops = pd.read_csv(file_path_stops)
trips = pd.read_csv(file_path_trips)

# stop_idが stops.txt に存在するものだけをフィルタリング
filtered_stop_times = stop_times[stop_times['stop_id'].isin(stops['stop_id'])]

# trip_idが trips.txt に存在するものだけをフィルタリング
filtered_stop_times = filtered_stop_times[filtered_stop_times['trip_id'].isin(trips['trip_id'])]

# フィルタリングしたデータをファイルに保存
filtered_stop_times.to_csv(file_path_stop_times, index=False)
