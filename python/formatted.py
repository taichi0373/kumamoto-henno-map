import pandas as pd
import sys

# コマンドライン引数からフォルダ名を取得
folder_name = sys.argv[1]

# ファイルパスを組み立てる
file_path1 = f'./{folder_name}/stop_times.txt'
file_path2 = f'./{folder_name}/stops.txt'

# データを読み込む
stop_times = pd.read_csv(file_path1)
stops = pd.read_csv(file_path2)

# stop_idが一致するものだけをフィルタリング
filtered_stop_times = stop_times[stop_times['stop_id'].isin(stops['stop_id'])]

# フィルタリングしたデータをファイルに保存
filtered_stop_times.to_csv(file_path1, index=False)
