import torch
from torch import nn
import numpy as np
import torch.optim as optim
import torch.nn.functional as F
from torch.utils.data import Dataset, DataLoader
from config import CACHE_SIZE_LIMIT

class CNN2(nn.Module):
    def __init__(self):
        super(CNN2, self).__init__()
        self.dropout = nn.Dropout(0.5)
        self.layer1 = nn.Sequential(nn.Conv2d(
            in_channels = 1,
            out_channels = 4,
            kernel_size = (7,1),
            stride = 1,
            padding =0),
        nn.ReLU(),
        self.dropout
        )
        self.layer2 = nn.Sequential(nn.Conv2d(
            in_channels = 4,
            out_channels = 8,
            kernel_size = (7,2),
            stride = 1,
            padding =0
            ),
        nn.ReLU(),
        self.dropout
        )
        self.fc = nn.Sequential( #704 for cachesize=100, 304 for 50
            nn.Linear(1504, 256)
            )
        self.out = nn.Sequential(
            nn.Linear(256, CACHE_SIZE_LIMIT)
        )

    def forward(self, x):
        # print(x.shape)
        x = self.layer1(x.view(-1, 1, CACHE_SIZE_LIMIT, 2))
        x = self.layer2(x)
        x = x.view(x.size(0), -1)
        # print(x.shape)
        x = self.fc(x)
        output = self.out(x)
        return output

# class CNN2(nn.Module):
#     def __init__(self):
#         super(CNN2, self).__init__()
#         self.dropout = nn.Dropout(0.5)
#         self.layer1 = nn.Sequential(nn.Conv2d(
#             in_channels = 1,
#             out_channels = 4,
#             kernel_size = (7,1),
#             stride = 1,
#             padding =0),
#         nn.ReLU(),
#         self.dropout
#         )
#         self.layer2 = nn.Sequential(nn.Conv2d(
#             in_channels = 4,
#             out_channels = 8,
#             kernel_size = (7,3),
#             stride = 1,
#             padding =0
#             ),
#         nn.ReLU(),
#         self.dropout
#         )
#         self.fc = nn.Sequential(
#             nn.Linear(704, 256)
#             )
#         self.out = nn.Sequential(
#             nn.Linear(256, CACHE_SIZE_LIMIT)
#         )

#     def forward(self, x):
#         x = self.layer1(x.view(-1, 1, CACHE_SIZE_LIMIT, 3))
#         x = self.layer2(x)
#         x = x.view(x.size(0), -1)
#         x = self.fc(x)
#         output = self.out(x)
#         return output